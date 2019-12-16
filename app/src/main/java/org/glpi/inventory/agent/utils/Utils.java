/**
 * ---------------------------------------------------------------------
 * GLPI Android Inventory Agent
 * Copyright (C) 2019 Teclib.
 *
 * https://glpi-project.org
 *
 * Based on Flyve MDM Inventory Agent For Android
 * Copyright © 2018 Teclib. All rights reserved.
 *
 * ---------------------------------------------------------------------
 *
 *  LICENSE
 *
 *  This file is part of GLPI Android Inventory Agent.
 *
 *  GLPI Android Inventory Agent is a subproject of GLPI.
 *
 *  GLPI Android Inventory Agent is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  GLPI Android Inventory Agent is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @copyright Copyright © 2019 Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/glpi-project/android-inventory-agent
 *  @link      https://glpi-project.org/glpi-network/
 *  ---------------------------------------------------------------------
 */

package org.glpi.inventory.agent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class Utils {

    public static void showAlertDialog(String message, Context context, final OnTaskCompleted callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onTaskSuccess();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public interface OnTaskCompleted {
        /**
         * if everything is Ok
         */
        void onTaskSuccess();
    }

    public static ArrayList<String> loadJsonHeader(String inventoryJson) {
        ArrayList<String> data = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(inventoryJson);
            JSONObject jsonRequest = json.getJSONObject("request");
            JSONObject jsonContent = jsonRequest.getJSONObject("content");

            Iterator<?> keys = jsonContent.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();

                if ( jsonContent.get(key) instanceof JSONArray) {
                    // add header
                    data.add(key.toUpperCase());
                }
            }
            return data;
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
        }
        return data;
    }

    @NonNull
    public static String[] getFormatTitle(ArrayList<String> listPreference) {
        listPreference.remove("");
        ArrayList<String> list = new ArrayList<>();
        for (String s : listPreference) {
            if (s.contains("STORAGES")) {
                list.add("Storage");
                continue;
            }
            if (s.contains("OPERATINGSYSTEM")) {
                list.add("OperatingSystem");
                continue;
            }
            if (s.contains("MEMORIES")) {
                list.add("Memory");
                continue;
            }
            if (s.contains("JVMS")) {
                list.add("Jvm");
                continue;
            }
            if (s.contains("SOFTWARES")) {
                list.add("Software");
                continue;
            }
            if (s.contains("USBDEVICES")) {
                list.add("Usb");
                continue;
            }
            if (s.contains("BATTERIES")) {
                list.add("Battery");
                continue;
            }
            list.add(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
        }
        return list.toArray(new String[list.size()]);
    }

    public static String getStringResourceByName(String name, Activity context) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(name, "string", packageName);
        String language = Locale.getDefault().getLanguage();
        return resId == 0 ? name : getLocaleStringResource(new Locale(language), resId, context);
    }

    private static String getLocaleStringResource(Locale requestedLocale, int resourceId, Context context) {
        String result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // use latest api
            Configuration config = new Configuration(context.getResources().getConfiguration());
            config.setLocale(requestedLocale);
            result = context.createConfigurationContext(config).getText(resourceId).toString();
        }
        else { // support older android versions
            Resources resources = context.getResources();
            Configuration conf = resources.getConfiguration();
            Locale savedLocale = conf.locale;
            conf.locale = requestedLocale;
            resources.updateConfiguration(conf, null);

            // retrieve resources from desired locale
            result = resources.getString(resourceId);

            // restore original locale
            conf.locale = savedLocale;
            resources.updateConfiguration(conf, null);
        }

        return result;
    }

}
