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

package org.glpi.inventory.agent.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.main.Main;
import org.glpi.inventory.agent.core.main.MainPresenter;
import org.glpi.inventory.agent.preference.GlobalParametersPreference;
import org.glpi.inventory.agent.preference.InventoryParametersPreference;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.LocalPreferences;
import org.glpi.inventory.agent.utils.LocalStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class ActivityMain extends AppCompatActivity
        implements Main.View{

    private Main.Presenter presenter;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    //private SharedPreferences sharedPreferences;
    private FloatingActionButton mainFab;
    private FloatingActionButton btn_settings;
    private FloatingActionButton btn_scheduler;
    private Boolean isOpen;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private TextView textview_settings, textview_scheduler;

    /*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strTime = intent.getStringExtra("time");
            if (sharedPreferences.getBoolean("autoStartInventory", false)) {
                toolbar.setSubtitle(strTime);
            } else {
                toolbar.setSubtitle("");
            }
        }
    };*/

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.setupInventoryAlarm(ActivityMain.this);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(broadcastReceiver,new IntentFilter(InventoryService.TIMER_RECEIVER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(ActivityMain.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                },
                1);

        if (!new LocalStorage(this).getDataBoolean("isFirstTime")) {
            loadCategories();
        }

        // Menu list
        ListView lst = findViewById(R.id.lst);

        // Setup the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);

        // layout
        fragmentManager = getSupportFragmentManager();

        // Setup Drawer Toggle of the Toolbar
        toolbar = findViewById(R.id.toolbar);

        // setup shared preference
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        presenter = new MainPresenter(this);
        Map<String, String> menuItem = presenter.setupDrawer(ActivityMain.this, lst);
        presenter.loadFragment(fragmentManager, toolbar, menuItem);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                presenter.loadFragment(fragmentManager, toolbar, presenter.getMenuItem().get(position));
                if(!presenter.getMenuItem().get(position).get("id").equals("1")){
                    if(isOpen){
                        openFab();
                    }
                    disableFab();
                }

            }
        });

        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //register to scheduled change
        IntentFilter timeAlarmChanged = new IntentFilter("timeAlarmChanged");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, timeAlarmChanged);

        //register to auto start change
        IntentFilter autoStartInventory = new IntentFilter("autoStartInventory");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, autoStartInventory);

        //FloatActionButton
        mainFab = findViewById(R.id.fab);
        btn_settings = findViewById(R.id.btn_settings);
        btn_scheduler = findViewById(R.id.btn_scheduler);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_anticlock);

        textview_settings =  findViewById(R.id.text_settings);
        textview_scheduler = findViewById(R.id.text_scheduler);
        isOpen = false;

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFab();
            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    openFab();
                }
                Intent miIntent = new Intent(ActivityMain.this, GlobalParametersPreference.class);
                ActivityMain.this.startActivity(miIntent);
            }
        });

        btn_scheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    openFab();
                }
                Intent miIntent = new Intent(ActivityMain.this, InventoryParametersPreference.class);
                ActivityMain.this.startActivity(miIntent);
            }
        });

    }

    private void disableFab(){
        mainFab.hide();
        btn_settings.hide();
        btn_scheduler.hide();
    }

    public void enableFab(){
        if(mainFab.getVisibility() != View.VISIBLE){
            mainFab.show();
        }
    }

    private void openFab(){
        if (isOpen) {
            textview_settings.setVisibility(View.INVISIBLE);
            textview_scheduler.setVisibility(View.INVISIBLE);
            btn_settings.startAnimation(fab_close);
            btn_scheduler.startAnimation(fab_close);
            mainFab.startAnimation(fab_anticlock);
            btn_settings.setClickable(false);
            btn_scheduler.setClickable(false);
            isOpen = false;
        } else {
            textview_settings.setVisibility(View.VISIBLE);
            textview_scheduler.setVisibility(View.VISIBLE);
            btn_settings.startAnimation(fab_open);
            btn_scheduler.startAnimation(fab_open);
            mainFab.startAnimation(fab_clock);
            btn_settings.setClickable(true);
            btn_scheduler.setClickable(true);
            isOpen = true;
        }
    }

    private void loadCategories() {
        String[] inventory = getResources().getStringArray(R.array.Inventory);
        ArrayList<String> categories = new ArrayList<>();
        Collections.addAll(categories, inventory);
        new LocalPreferences(ActivityMain.this).saveCategories(categories);
        new LocalStorage(ActivityMain.this).setDataBoolean("isFirstTime", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityMain.this, message, getString(R.string.permission_snack_ok), true);
    }

    /*@Override
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
    }*/

    @Override
    public void onBackPressed() {
        if(!isOpen){
            super.onBackPressed();
        }else{
            openFab();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    String message = getResources().getString(R.string.permission_error_result);
                    Helpers.snackClose(ActivityMain.this, message, getString(R.string.permission_snack_ok), true);
                }
            }
        }
    }
}
