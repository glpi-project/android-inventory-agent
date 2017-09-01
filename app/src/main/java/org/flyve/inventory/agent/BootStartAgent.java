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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.flyvemdm.inventory.categories.StringUtils;

public class BootStartAgent extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        boolean shouldAutoStart = prefs.getBoolean("boot", false);

        FusionInventory.log(this, String.format("Intent %s Category %s",
                intent.getAction(),
                StringUtils.join(intent.getCategories(), " , ")), Log.INFO);

        if (shouldAutoStart) {
            FusionInventory.log(this,
                    "FusionInventory Agent is being started automatically",
                    Log.INFO);
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("org.fusioninventory.Agent");
            ctx.startService(serviceIntent);
        } else {
            FusionInventory.log(this,
                    "FusionInventory Agent will not be started automatically",
                    Log.INFO);
        }
    }
}
