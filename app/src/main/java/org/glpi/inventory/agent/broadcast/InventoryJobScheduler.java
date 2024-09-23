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

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.ui.ActivityMain;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.HttpInventory;
import org.glpi.inventory.agent.utils.LocalPreferences;

import java.util.ArrayList;
import java.util.Calendar;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class InventoryJobScheduler extends JobService {

    public static final int INVENTORY_JOB_ID = 4492015;
    private static final String NOTIFICATION_CHANNEL_ID = "org.glpi.inventory.agent";

    @Override
    public boolean onStartJob(final JobParameters params) {
        HandlerThread handlerThread = new HandlerThread("SomeOtherThread");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Run task" + Calendar.getInstance().getTime());
                doInventory();
                jobFinished(params, true);
            }
        });

        return true;
    }

    private void doInventory() {
        Context context = getApplicationContext();
        AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Launch inventory from JobScheduler " + Calendar.getInstance().getTime());

        // check if autoStartInventory is deactivated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPreferences.getBoolean("autoStartInventory", false)) {
            AgentLog.d("GLPI-AGENT-JOBSCHEDULER : The inventory will not be send, is deactivated");
            return;
        }

        showPersistentNotification();

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
                        if(!model.getSerial().trim().isEmpty()) {
                            data = data.replaceAll("<SSN>(.*)</SSN>","<SSN>" + model.getSerial() + "</SSN>");
                        }
                        httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                            @Override
                            public void onTaskSuccess(String data) {
                                AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Inventory Success");
                                Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.inventory_notification_sent));
                                //Helpers.sendAnonymousData(context.getApplicationContext(), inventory);
                            }

                            @Override
                            public void onTaskError(String error) {
                                AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Inventory error");
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
            AgentLog.d("GLPI-AGENT-JOBSCHEDULER : " + context.getResources().getString(R.string.inventory_no_server));
        }
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        AgentLog.d("GLPI-AGENT-JOBSCHEDULER : onStopJob() was called");
        return true;
    }

    private void showPersistentNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // create channel if needed
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan = null;
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_is_running), NotificationManager.IMPORTANCE_DEFAULT);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager.createNotificationChannel(chan);
        }

        // create intent to redirect to notification to settings
        Intent notificationIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            notificationIntent.putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID);
            notificationIntent.putExtra("app_package", getPackageName());
            notificationIntent.putExtra("app_uid", getApplicationInfo().uid);
            notificationIntent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else {
            notificationIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            notificationIntent.setData(Uri.parse("package:" + getPackageName()));
        }
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Intent appIntent = new Intent(this, ActivityMain.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent appIntentRedirect = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_IMMUTABLE);

        // create notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notification.setOngoing(false);
        notification.setSmallIcon(R.drawable.ic_stat);
        notification.setContentTitle(getString(R.string.app_is_running));
        notification.setContentText(getString(R.string.agent_description));
        notification.setCategory(Notification.CATEGORY_EVENT);
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.app_is_running_extend)));
        notification.setContentIntent(appIntentRedirect);
        notification.addAction(R.drawable.ic_stat, getString(R.string.disable_notification), notificationPendingIntent);

        // notify
        manager.notify(1, notification.build());
    }
}
