/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
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
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

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
            FlyveLog.e(ex.getMessage());
        }
        return data;
    }


    public static String getStringResourceByName(String aString, Activity activity) {
        String packageName = activity.getPackageName();
        int resId = activity.getResources().getIdentifier(aString, "string", packageName);
        return activity.getString(resId);
    }

}
