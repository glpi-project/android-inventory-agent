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
 * @link      https://github.com/flyvemdm/flyve-mdm-android-inventory-agent
 * @link      http://www.glpi-project.org/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.flyvemdm.inventory.InventoryTask;

import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.HttpInventory;

public class AutoInventory extends Service {

    @Override
    public void onCreate() {
        loadInventory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FlyveLog.log(this, "Received start id " + startId + ": " + intent, Log.INFO);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void loadInventory() {
        InventoryTask inventory = new InventoryTask(this, "Inventory-Agent-Android_v1.0");
        inventory.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                HttpInventory httpInventory = new HttpInventory(AutoInventory.this);
                httpInventory.sendInventory( data );
            }

            @Override
            public void onTaskError(Throwable error) {
                Toast.makeText(AutoInventory.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
