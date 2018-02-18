package org.flyve.inventory.agent.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.MainActivity;
import org.flyve.inventory.agent.R;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/*
 *   Copyright © 2017 Teclib. All rights reserved.
 *
 *   This file is part of android-inventory-agent
 *
 * android-inventory-agent is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @date      2/10/17
 * @copyright Copyright © 2017 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */
public class Helpers {

    /**
     * private constructor
     */
    private Helpers() {
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
        if(fail) {
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
        Boolean val = sharedPreferences.getBoolean("anonymousData",false);
        if(val) {
            inventoryTask.getJSONWithoutPrivateData(new InventoryTask.OnTaskCompleted() {
                @Override
                public void onTaskSuccess(String s) {
                    FlyveLog.d(s);
                    ConnectionHTTP.syncWebData("https://inventory.chollima.pro/-1001180163835/", s);
                }

                @Override
                public void onTaskError(Throwable throwable) {
                    FlyveLog.e(throwable.getMessage());
                }
            });
        }
    }


    public static boolean isForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public static void sendToNotificationBar(Context context, String message) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent piResult = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(piResult)
                .setPriority(Notification.PRIORITY_HIGH);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_notification_white);
        } else {
            builder.setSmallIcon(R.drawable.icon);
        }


        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(121, builder.build());
    }

    public static void share(Context context, String message, int type){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if(type==1) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/FlyveMDM/Inventory.json"));
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/FlyveMDM/Inventory.xml"));
        }
        context.startActivity(Intent.createChooser(intent, "Share your inventory"));
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
        String app = "Flyve MDM";

        // Inventory Agent/0.2.0[568] (Linux; Android 4.0.4; Flyve MDM)
        return name + versionApp + " (" + type + "; " + versionAndroid + "; " + app + ")";
    }

}
