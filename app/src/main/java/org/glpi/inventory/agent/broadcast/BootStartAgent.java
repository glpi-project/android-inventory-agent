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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.flyve.inventory.InventoryLog;
import org.glpi.inventory.agent.service.InventoryService;
import org.glpi.inventory.agent.ui.ActivityMain;
import org.glpi.inventory.agent.utils.AgentLog;

public class BootStartAgent extends BroadcastReceiver {

    TimeAlarm alarm = new TimeAlarm();

    /**
     * It sets an alarm after the user has finished booting
     * @param context in which the receiver is running
     * @param intent being received
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action==null) {
            return;
        }

        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        if (customSharedPreference.getBoolean("boot", false)) {
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent myIntent = new Intent(context, InventoryService.class);
                    context.startForegroundService(myIntent);
                } else {
                    Intent myIntent = new Intent(context, ActivityMain.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(myIntent);
                }
            }catch(Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if(customSharedPreference.getBoolean("autoStartInventory", false)){
            alarm.setAlarm(context);
        }

    }
}
