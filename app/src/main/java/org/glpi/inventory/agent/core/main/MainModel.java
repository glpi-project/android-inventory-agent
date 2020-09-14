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

package org.glpi.inventory.agent.core.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.flyve.inventory.InventoryLog;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.adapter.DrawerAdapter;
import org.glpi.inventory.agent.service.SendInventoryWorker;
import org.glpi.inventory.agent.ui.FragmentAbout;
import org.glpi.inventory.agent.ui.FragmentHelp;
import org.glpi.inventory.agent.ui.FragmentHome;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.LocalStorage;
import androidx.appcompat.widget.Toolbar;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainModel implements Main.Model {

    private Main.Presenter presenter;
    private ArrayList<HashMap<String, String>> arrDrawer;
    public static final String TAG_MY_WORK = "inventory_scheduled";

    public MainModel(Main.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupInventoryAlarm(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timeInventory = sharedPreferences.getString("timeInventory", "day");

        boolean autoStartInventory = sharedPreferences.getBoolean("autoStartInventory", false);
        if(!autoStartInventory) {
            InventoryLog.d("WORKER -> schedule inventory is disable -> cancel worker if exist");
            WorkManager.getInstance(context).cancelAllWorkByTag(TAG_MY_WORK);
            return;
        }

        //day configuration
        int interval = 1;
        int backOffDelay = 2;

        // week by default
        if (timeInventory != null && timeInventory.equalsIgnoreCase("week")) {
            backOffDelay = 5;
            interval = 7;
        }

        // month
        if (timeInventory != null && timeInventory.equalsIgnoreCase("month")) {
            backOffDelay = 5;
            interval = 3;
        }

        InventoryLog.d("WORKER -> configure worker for "+timeInventory+" interval");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(SendInventoryWorker.class, interval, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, backOffDelay, TimeUnit.HOURS) //retry every 2 hours
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(TAG_MY_WORK, ExistingPeriodicWorkPolicy.REPLACE,saveRequest);
    }

    public void loadFragment(FragmentManager fragmentManager, Toolbar toolbar, Map<String, String> menuItem) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Home
        if (menuItem.get("id").equals("1")) {
            FragmentHome f = new FragmentHome();
            f.setToolbar(toolbar);
            fragmentTransaction.replace(R.id.containerView, f).commit();
        }

        // Help
        if (menuItem.get("id").equals("4")) {
            FragmentHelp f = new FragmentHelp();
            f.setToolbar(toolbar);
            fragmentTransaction.addToBackStack("help");
            fragmentTransaction.replace(R.id.containerView, f).commit();
        }

        // About
        if (menuItem.get("id").equals("5")) {
            FragmentAbout f = new FragmentAbout();
            f.setToolbar(toolbar);
            fragmentTransaction.addToBackStack("about");
            fragmentTransaction.replace(R.id.containerView, f).commit();
        }
    }

    public List<HashMap<String, String>> getMenuItem() {
        return arrDrawer;
    }

    public Map<String, String> setupDrawer(Activity activity, ListView lst) {
        arrDrawer = new ArrayList<>();

        // Information
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("name", activity.getResources().getString(R.string.drawer_inventory));
        map.put("img", "ic_info");
        arrDrawer.add(map);

        // Help
        map = new HashMap<>();
        map.put("id", "4");
        map.put("name", activity.getResources().getString(R.string.drawer_help));
        map.put("img", "ic_help");
        arrDrawer.add(map);

        // About
        map = new HashMap<>();
        map.put("id", "5");
        map.put("name", activity.getResources().getString(R.string.drawer_about));
        map.put("img", "ic_about");
        arrDrawer.add(map);

        try {
            // load adapter
            DrawerAdapter adapter = new DrawerAdapter(activity, arrDrawer);
            lst.setAdapter(adapter);

            // Select Information on load //
            return arrDrawer.get(0);
        } catch(Exception ex) {
            AgentLog.e(ex.getMessage());
        }

        return null;
    }
}
