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
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.core.main.Main;
import org.flyve.inventory.agent.core.main.MainPresenter;
import org.flyve.inventory.agent.service.InventoryService;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.LocalStorage;

import java.util.Map;

public class ActivityMain extends AppCompatActivity
        implements Main.View, SharedPreferences.OnSharedPreferenceChangeListener {

    private Main.Presenter presenter;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private android.support.v7.widget.Toolbar toolbar;
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strTime = intent.getStringExtra("time");
            if (sharedPreferences.getBoolean("autoStartInventory", true)) {
                toolbar.setSubtitle(strTime);
            } else {
                toolbar.setSubtitle("");
            }
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LocalStorage(context).setDataBoolean("changeSchedule", true);
            presenter.setupInventoryAlarm(ActivityMain.this);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(InventoryService.TIMER_RECEIVER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menu list
        ListView lst = findViewById(R.id.lst);

        // Setup the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);

        // layout
        fragmentManager = getSupportFragmentManager();

        // Setup Drawer Toggle of the Toolbar
        toolbar = findViewById(R.id.toolbar);

        // setup shared preference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        presenter = new MainPresenter(this);
        Map<String, String> menuItem = presenter.setupDrawer(ActivityMain.this, lst);
        presenter.loadFragment(fragmentManager, toolbar, menuItem);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                presenter.loadFragment(fragmentManager, toolbar, presenter.getMenuItem().get(position));
            }
        });

        presenter.setupInventoryAlarm(ActivityMain.this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        IntentFilter timeAlarmChanged = new IntentFilter("timeAlarmChanged");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, timeAlarmChanged);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityMain.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("autoStartInventory")) {
            if (!sharedPreferences.getBoolean("autoStartInventory", false)) {
                if (this.sharedPreferences.getBoolean("autoStartInventory", false)) {
                    toolbar.setSubtitle("");
                } else {
                    toolbar.setSubtitle("");
                }
            }
        }
    }
}
