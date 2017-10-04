/*
 * Copyright (C) 2017 Teclib'
 *
 * This file is part of Flyve MDM Inventory Agent Android.
 *
 * Flyve MDM Inventory Agent Android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
 *
 * Flyve MDM Android is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM Inventory Agent Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez - rafaelje
 * @copyright Copyright (c) 2017 Flyve MDM
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent/
 * @link      http://www.glpi-project.org/
 * @link      https://flyve-mdm.com/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.flyve.inventory.InventoryTask;

import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;

import java.util.Calendar;

public class TimeAlarm extends BroadcastReceiver {

    /**
     * If the success XML is created, it sends the inventory
     * @param Context the context in which the receiver is running
     * @param Intent the intent being received
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        InventoryTask inventory = new InventoryTask(context, "Inventory-Agent-Android_v1.0");
        inventory.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                HttpInventory httpInventory = new HttpInventory(context);
                httpInventory.sendInventory(data, new HttpInventory.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        if(!Helpers.isForeground()) {
                            Helpers.sendToNotificationBar(context, context.getResources().getString(R.string.inventory_notification_sent));
                        }
                        FlyveLog.d(data);
                    }

                    @Override
                    public void onTaskError(String error) {
                        if(!Helpers.isForeground()) {
                            Helpers.sendToNotificationBar(context, context.getResources().getString(R.string.inventory_notification_fail));
                        }
                        FlyveLog.e(error);
                    }
                });
            }

            @Override
            public void onTaskError(Throwable error) {
                FlyveLog.e(error.getMessage());
            }
        });
    }

    /**
     * Schedules the alarm
     * @param Context the context
     */
    public void setAlarm(Context context) {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, TimeAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String timeInventory = customSharedPreference.getString("timeInventory", "Week");

        Calendar cal = Calendar.getInstance();
        if (timeInventory.equals("Day")) {
            cal.set(Calendar.HOUR_OF_DAY, 18);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if(timeInventory.equals("Week")) {
            cal.set(Calendar.DAY_OF_WEEK, 1);
            cal.set(Calendar.HOUR_OF_DAY, 18);
            cal.set(Calendar.MINUTE, 33);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if(timeInventory.equals("Month")) {
            cal.set(Calendar.WEEK_OF_MONTH, 1);
            cal.set(Calendar.DAY_OF_WEEK, 1);
            cal.set(Calendar.HOUR_OF_DAY, 18);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

    /**
     * Removes the alarm with a matching argument
     * @param Context the context
     */
    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, TimeAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
