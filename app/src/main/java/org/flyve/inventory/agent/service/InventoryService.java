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
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.schema.ServerSchema;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;
import org.flyve.inventory.agent.utils.LocalPreferences;
import org.flyve.inventory.agent.utils.LocalStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InventoryService extends Service {

    public static final String TIMER_RECEIVER = "org.flyve.inventory.service.timer";

    private Handler mHandler;
    Calendar calendar;
    long longDate;
    Date dateCurrent, dateDiff;
    LocalStorage cache;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        mHandler = new Handler();

        cache = new LocalStorage(getApplicationContext());
        calendar = Calendar.getInstance();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);
        intent = new Intent(TIMER_RECEIVER);
        startForeground(1, new Notification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "org.flyve.inventory.agent";
        String channelName = "Inventory Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    calendar = Calendar.getInstance();
                    longDate = calendar.getTime().getTime();
                    Log.i("strDate", String.valueOf(longDate));
                    twoDatesBetweenTime();
                }

            });
        }
    }

    private void twoDatesBetweenTime() {

        try {
            dateCurrent = new Date(longDate);
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

        try {
            dateDiff = new Date(cache.getDataLong("data"));
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

        try {
            long diff = dateDiff.getTime() - dateCurrent.getTime();

            long days = TimeUnit.MILLISECONDS.toDays(diff);
            diff -= TimeUnit.DAYS.toMillis(days);

            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            diff -= TimeUnit.HOURS.toMillis(hours);

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            diff -= TimeUnit.MINUTES.toMillis(minutes);

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

            if (seconds + hours + minutes + days > 0) {
                String strTesting;
                if (days != 0)
                    strTesting = days + " days  " + hours + ":" + minutes + ":" + seconds;
                else
                    strTesting = hours + ":" + minutes + ":" + seconds;
                updateTime(strTesting);
            } else {
                sendInventory();
                setupInventorySchedule();
            }
        } catch (Exception e) {
            mTimer.cancel();
            mTimer.purge();
        }

    }

    private void setupInventorySchedule() {
        calendar = Calendar.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String timeInventory = sharedPreferences.getString("timeInventory", "week");

        // week by default
        if (timeInventory.equalsIgnoreCase("week")) {
            calendar.add(Calendar.DATE, 7);
        }

        if(timeInventory.equalsIgnoreCase("day")) {
            calendar.add(Calendar.DATE, 1);
        }

        if(timeInventory.equalsIgnoreCase("month")) {
            calendar.add(Calendar.DATE, 30);
        }

        long dateTime = calendar.getTime().getTime();

        cache = new LocalStorage(getApplicationContext());
        cache.setDataLong("data", dateTime);
    }

    private void sendInventory() {
        final Context context = getApplicationContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean autoStartInventory = sharedPreferences.getBoolean("autoStartInventory", false);
        if(!autoStartInventory) {  return; }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        final InventoryTask inventory = new InventoryTask(context.getApplicationContext(), Helpers.getAgentDescription(context), true);
        final HttpInventory httpInventory = new HttpInventory(context.getApplicationContext());
        ArrayList<String> serverArray = new LocalPreferences(context).loadServer();
        if (!serverArray.isEmpty()) {
            for (String serverName : serverArray) {
                final ServerSchema model = httpInventory.setServerModel(serverName);
                inventory.getXML(new InventoryTask.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                            @Override
                            public void onTaskSuccess(String data) {
                                Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.inventory_notification_sent));
                                Helpers.sendAnonymousData(context.getApplicationContext(), inventory);
                            }

                            @Override
                            public void onTaskError(String error) {
                                Helpers.sendToNotificationBar(context.getApplicationContext(), error);
                                FlyveLog.e(error);
                            }
                        });
                    }

                    @Override
                    public void onTaskError(Throwable error) {
                        FlyveLog.e(error.getMessage());
                        Helpers.sendToNotificationBar(context, context.getResources().getString(R.string.inventory_notification_fail));
                    }
                });
            }
        } else {
            Helpers.sendToNotificationBar(context.getApplicationContext(), context.getResources().getString(R.string.no_servers_added));
            FlyveLog.e(context.getResources().getString(R.string.no_servers_added));
        }

        wl.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service finish", "Finish");
        getApplicationContext().startService(new Intent(getApplicationContext(), InventoryService.class));
    }

    private void updateTime(String strTime) {
        intent.putExtra("time", strTime);
        sendBroadcast(intent);
    }
}
