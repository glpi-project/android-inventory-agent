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

package org.glpi.inventory.agent.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.HttpInventory;
import org.glpi.inventory.agent.utils.LocalPreferences;

import java.util.ArrayList;

public class TimeAlarm extends BroadcastReceiver {

    /**
     * If the success XML is created, it sends the inventory
     * @param context in which the receiver is running
     * @param intent being received
     */
    @Override
    public void onReceive(final Context context, Intent intent) {

        // check if is deactivated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean val = sharedPreferences.getBoolean("autoStartInventory", false);
        if (!val) {
            AgentLog.d("The inventory will not be send, is deactivated");
            return;
        }

        AgentLog.d("Launch inventory from alarm");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        final InventoryTask inventory = new InventoryTask(context.getApplicationContext(), Helpers.getAgentDescription(context), true);
        final HttpInventory httpInventory = new HttpInventory(context.getApplicationContext());
        ArrayList<String> serverArray = new LocalPreferences(context).loadServer();
        if (!serverArray.isEmpty()) {
            for (final String serverName : serverArray) {
                final ServerSchema model = httpInventory.setServerModel(serverName);
                inventory.setTag(model.getTag());
                inventory.setAssetItemtype(model.getItemtype());
                inventory.getXML(new InventoryTask.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        ServerSchema model = httpInventory.setServerModel(serverName);
                        if(!model.getSerial().isEmpty()) {
                            data = data.replaceAll("<SSN>(.*)</SSN>","<SSN>" + model.getSerial() + "</SSN>");
                        }
                        httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                            @Override
                            public void onTaskSuccess(String data) {
                                Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.inventory_notification_sent));
                                //Helpers.sendAnonymousData(context.getApplicationContext(), inventory);
                            }

                            @Override
                            public void onTaskError(String error) {
                                Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.inventory_notification_fail));
                                AgentLog.e(error);
                            }
                        });
                    }

                    @Override
                    public void onTaskError(Throwable error) {
                        AgentLog.e(error.getMessage());
                        Helpers.sendToNotificationBar(context, context.getResources().getString(R.string.inventory_notification_fail));
                    }
                });
            }
        } else {
            AgentLog.e(context.getResources().getString(R.string.inventory_no_server));
        }

        wl.release();
    }

    /**
     * Schedules the alarm
     * @param context
     */
    public void setAlarm(Context context) {

        AgentLog.d("Set Alarm");

        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, TimeAlarm.class);
        i.setAction("org.glpi.inventory.agent.ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String timeInventory = customSharedPreference.getString("timeInventory", "Week");

        int time = 60 * 1000;

        if (timeInventory.equals("Day")) {
            time = 24 * 60 * 60 * 1000;
            AgentLog.d("Alarm Daily");
        } else if(timeInventory.equals("Week")) {
            time = 7 * 24 * 60 * 60 * 1000;
            AgentLog.d("Alarm Weekly");
        } else if(timeInventory.equals("Month")) {
            time = 30 * 24 * 60 * 60 * 1000;
            AgentLog.d("Alarm Monthly");
        }

        try {
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, pi);
        } catch (NullPointerException ex) {
            AgentLog.e(ex.getMessage());
        }
    }

    /**
     * Removes the alarm with a matching argument
     * @param context
     */
    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, TimeAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
