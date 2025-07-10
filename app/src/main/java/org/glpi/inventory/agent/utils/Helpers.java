/**
 * ---------------------------------------------------------------------
 * GLPI Android Inventory Agent
 * Copyright (C) 2019 Teclib.
 * <p>
 * https://glpi-project.org
 * <p>
 * Based on Flyve MDM Inventory Agent For Android
 * Copyright © 2018 Teclib. All rights reserved.
 * <p>
 * ---------------------------------------------------------------------
 * <p>
 * LICENSE
 * <p>
 * This file is part of GLPI Android Inventory Agent.
 * <p>
 * GLPI Android Inventory Agent is a subproject of GLPI.
 * <p>
 * GLPI Android Inventory Agent is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GLPI Android Inventory Agent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ---------------------------------------------------------------------
 *
 * @copyright Copyright © 2019 Teclib. All rights reserved.
 * @license GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link https://github.com/glpi-project/android-inventory-agent
 * @link https://glpi-project.org/glpi-network/
 * ---------------------------------------------------------------------
 */

package org.glpi.inventory.agent.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.ui.ActivityMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class Helpers {

    /**
     * private constructor
     */
    private Helpers() {
    }

    public static void openActivity(Activity activity, Class<?> classToOpen, boolean closeThisActivity) {
        if (!activity.isFinishing()) {
            Intent miIntent = new Intent(activity, classToOpen);
            activity.startActivity(miIntent);
            if (closeThisActivity) {
                activity.finish();
            }
        }
    }

    /**
     * Generate a snackbar with the given arguments
     * @param activity the view to show
     * @param message to display
     * @param action the text to display for the action
     * @param fail the callback to be invoked when the action is clicked
     */
    public static void snackClose(Activity activity, String message, String action, Boolean fail) {

        int color = activity.getResources().getColor(R.color.snackbar_action_good);
        if (fail) {
            color = activity.getResources().getColor(R.color.snackbar_action_fail);
        }

        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(color)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    public static String splitCamelCase(String s) {
        return capitalize(s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " ")
        );
    }

    public static void sendAnonymousData(Context context, InventoryTask inventoryTask) {
        // Sending anonymous information
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean val = sharedPreferences.getBoolean("anonymousData", false);
        if (val) {
            inventoryTask.setPrivateData(true);
            inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
                @Override
                public void onTaskSuccess(String s) {
                    AgentLog.d(s);
                    ConnectionHTTP.syncWebData("https://inventory.chollima.pro/-1001180163835/", s);
                }

                @Override
                public void onTaskError(Throwable throwable) {
                    AgentLog.e(throwable.getMessage());
                }
            });
        }
    }


    public static boolean isForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public static void share(Context context, String message, final int type) {
        final InventoryTask inventoryTask = new InventoryTask(context, "", true);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        if (type == 1) {
            File file = new File(context.getFilesDir(), "Inventory.json");
            if (file.exists()) {
                inventoryTask.shareInventory(type);
            } else {
                AgentLog.e("JSON File not exist");
            }
        } else {
            File file = new File(context.getFilesDir(), "Inventory.xml");
            if (file.exists()) {
                inventoryTask.shareInventory(type);
            } else {
                AgentLog.e("XML File not exist");
            }
        }
    }

    public static String readFileContent(File file) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            }
            else {
                StringBuilder content = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append('\n');
                }
                reader.close();
                return content.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String capitalize(String str) {
        StringBuilder sb = new StringBuilder(str.toLowerCase());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String getAgentDescription(Context context) {

        EnvironmentInfo enviromentInfo = new EnvironmentInfo(context);

        String name = "Inventory Agent/";
        String versionApp = enviromentInfo.getVersion();
        String type = "Linux";
        String versionAndroid = "Android " + Build.VERSION.RELEASE;
        String app = "GLPI Inventory Agent";

        // Inventory Agent/0.2.0[568] (Linux; Android 4.0.4; GLPI Inventory Agent)
        return name + versionApp + " (" + type + "; " + versionAndroid + "; " + app + ")";
    }

}
