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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.flyve.inventory.agent.utils.FlyveLog;

public class InventoryService extends Service {

    TimeAlarm alarm = new TimeAlarm();

    /**
     * Called by the system when the service is first created
     * It calls the method from the parent
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed
     * It calls the method from the parent
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().startService(new Intent(getApplicationContext(), InventoryService.class));
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling the method startService(Intent)
     * @param Intent the intent supplied to start the service
     * @param int flags additional data about this start request
     * @param int startID a unique integer representing this specific request to start
     * @return constant START_STICKY
     * @see https://developer.android.com/reference/android/app/Service.html#START_STICKY Documentation of the Constant
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FlyveLog.log(this, "Received start id " + startId + ": " + intent, Log.INFO);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        alarm.setAlarm(this);
        return START_STICKY;
    }

    /**
     * It is called when the service is started
     * @param Intent the intent supplied
     * @param int startId a unique integer representing this specific request to start
     * @see https://developer.android.com/reference/android/app/Service.html#onStart(android.content.Intent, int)
     */
    @Override
    public void onStart(Intent intent, int startId) {
        alarm.setAlarm(this);
    }

    /**
     * Return the communication channel to the service
     * @param Intent the intent that was used to bind to this service
     * @return IBinder null if clients cannot bind to the service 
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
