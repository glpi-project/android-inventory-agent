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
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.enterprise.feedback.KeyedAppState;
import androidx.enterprise.feedback.KeyedAppStatesReporter;
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
import org.glpi.inventory.agent.broadcast.InventoryJobScheduler;
import org.glpi.inventory.agent.core.main.Main;
import org.glpi.inventory.agent.core.main.MainPresenter;
import org.glpi.inventory.agent.preference.GlobalParametersPreference;
import org.glpi.inventory.agent.preference.InventoryParametersPreference;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.LocalPreferences;
import org.glpi.inventory.agent.utils.LocalStorage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActivityMain extends AppCompatActivity
        implements Main.View, SharedPreferences.OnSharedPreferenceChangeListener {

    private Main.Presenter presenter;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton mainFab;
    private FloatingActionButton btn_settings;
    private FloatingActionButton btn_scheduler;
    private Boolean isOpen;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private TextView textview_settings, textview_scheduler;
    InventoryJobScheduler alarm = new InventoryJobScheduler();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LocalStorage(context).setDataBoolean("changeSchedule", true);
            presenter.setupInventoryAlarm(ActivityMain.this);
        }
    };

    private BroadcastReceiver appRestrictionChange = null;

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter restrictionsFilter = new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
        BroadcastReceiver appRestrictionChange = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                resolveRestrictions();
            }
        };

        if (Build.VERSION.SDK_INT >= 34) {
            registerReceiver(appRestrictionChange, restrictionsFilter, RECEIVER_EXPORTED);
        } else {
            registerReceiver(appRestrictionChange, restrictionsFilter);
        }

        if (sharedPreferences.getBoolean("autoStartInventory", false)) {
            String label =  getString(R.string.schedule_inventory_each);
            String frequency =  "weekly";
            switch(sharedPreferences.getString("timeInventory", "Week")){
                case "Day":
                    frequency = "daily";
                    break;
                case "Month":
                    frequency = "monthly";
                    break;
                default:
                    frequency = "weekly";
                    break;
            }

            toolbar.setSubtitle(label + ' ' + frequency);
        } else {
            toolbar.setSubtitle("Scheduled inventory not configured");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (appRestrictionChange != null) {
            unregisterReceiver(appRestrictionChange);
            appRestrictionChange = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resolveRestrictions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(broadcastReceiver);
    }

    private void checkPermissions() {
        List<String> permissionsList = new ArrayList<>(Arrays.asList(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA
        ));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        String[] permissions = permissionsList.toArray(new String[0]);

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();

        setContentView(R.layout.activity_main);

        if (!new LocalStorage(this).getDataBoolean("isFirstTime")) {
            loadCategories();
        }

        scheduleJob();

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
                if(!presenter.getMenuItem().get(position).get("id").equals("1")){
                    if(isOpen){
                        openFab();
                    }
                    disableFab();
                }

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

    @Override
    public void onBackPressed() {
        if(!isOpen){
            super.onBackPressed();
        }else{
            openFab();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            String message = getResources().getString(R.string.permission_error_result);
            Helpers.snackClose(ActivityMain.this, message, getString(R.string.permission_snack_ok), true);
        }
    }

    public static void enterpriseFeedback(Context context,
                                          String key,
                                          String message,
                                          String data,
                                          int severity) {
        KeyedAppStatesReporter keyedAppStatesReporter = KeyedAppStatesReporter.create(context);
        KeyedAppState keyedAppStateMessage = KeyedAppState.builder()
                .setSeverity(severity)
                .setKey(key)
                .setMessage(message)
                .setData(data)
                .build();
        List<KeyedAppState> list = new ArrayList<>();
        list.add(keyedAppStateMessage);
        keyedAppStatesReporter.setStates(list);
    }

    private void resolveRestrictions() {
        AgentLog.e("EMM - START resolve restrictions");
        RestrictionsManager myRestrictionsMgr = null;
        myRestrictionsMgr = (RestrictionsManager) getSystemService(Context.RESTRICTIONS_SERVICE);
        Bundle appRestrictions = myRestrictionsMgr.getApplicationRestrictions();

        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = customSharedPreference.edit();

        if (appRestrictions.containsKey("automatic_inventory")) {
            editor.putBoolean("autoStartInventory", appRestrictions.getBoolean("automatic_inventory"));
            enterpriseFeedback(getApplicationContext(), "automatic_inventory", "automatic_inventory option set successfully", appRestrictions.getBoolean("automatic_inventory") ? "true" : "false", KeyedAppState.SEVERITY_INFO);
            AgentLog.e("EMM - set automatic inventory to " + appRestrictions.getBoolean("automatic_inventory"));
            editor.apply();
        }

        if (appRestrictions.containsKey("frequency")) {
            editor.putString("timeInventory", appRestrictions.getString("frequency", "Week"));
            enterpriseFeedback(getApplicationContext(), "frequency", "frequency option set successfully", appRestrictions.getString("frequency"), KeyedAppState.SEVERITY_INFO);
            AgentLog.e("EMM - set frequency to " + appRestrictions.getString("frequency", "Week"));
            editor.apply();
        }

        if (appRestrictions.containsKey("auto_start_on_boot")) {
            editor.putBoolean("boot", appRestrictions.getBoolean("auto_start_on_boot"));
            enterpriseFeedback(getApplicationContext(), "auto_start_on_boot", "auto_start_on_boot option set successfully", appRestrictions.getBoolean("auto_start_on_boot") ? "true" : "false", KeyedAppState.SEVERITY_INFO);
            AgentLog.e("EMM - set auto start on boot to " + appRestrictions.getBoolean("auto_start_on_boot"));
            editor.apply();
        }

        Parcelable[] parcelables = appRestrictions.getParcelableArray("server_configuration_list");
        if (parcelables != null && parcelables.length > 0) {
            final Context context = getApplicationContext();
            for (int i = 0; i < parcelables.length; i++) {
                Bundle serverConfig = (Bundle) parcelables[i];
                JSONObject jsonServerConfig = new JSONObject();
                LocalPreferences preferences = new LocalPreferences(context);

                if (serverConfig.getString("server_url").isEmpty()) {
                    enterpriseFeedback(getApplicationContext(), "server_url", "Error server URL is mandatory -> ", serverConfig.getString("server_url"), KeyedAppState.SEVERITY_ERROR);
                    AgentLog.e("EMM - server url is mandatory");
                    continue;
                }

                try {
                    jsonServerConfig.put("address", serverConfig.getString("server_url"));

                    String tag = serverConfig.containsKey("server_tag") ? serverConfig.getString("server_tag") : "";
                    jsonServerConfig.put("tag", tag);

                    String login = serverConfig.containsKey("server_login") ? serverConfig.getString("server_login") : "";
                    jsonServerConfig.put("login", login);

                    String pass = serverConfig.containsKey("server_password") ? serverConfig.getString("server_password") : "";
                    jsonServerConfig.put("pass", pass);

                    String itemType = serverConfig.containsKey("server_itemtype") ? serverConfig.getString("server_itemtype") : "Computer";
                    jsonServerConfig.put("itemtype", itemType);

                    String serial = serverConfig.containsKey("server_custom_asset_serial") ? serverConfig.getString("server_custom_asset_serial") : "";
                    jsonServerConfig.put("serial", serial);

                    AgentLog.e("EMM - Receive the following configuration '" + jsonServerConfig.toString());

                    JSONObject local_server = preferences.loadJSONObject(serverConfig.getString("server_url"));
                    AgentLog.e("EMM - Try to load '" + serverConfig.getString("server_url") + "' server if exist");
                    AgentLog.e("EMM - Found '" + local_server.toString() + "'");
                    AgentLog.e("EMM - Exist ? -> '" + !local_server.toString().equals("{}") + "'");

                    if (local_server.toString().equals("{}")) {
                        ArrayList<String> serverArray = preferences.loadServer();
                        serverArray.add(serverConfig.getString("server_url"));
                        preferences.saveServer(serverArray);
                        preferences.saveJSONObject(serverConfig.getString("server_url"), jsonServerConfig);
                        enterpriseFeedback(getApplicationContext(), "server_url", "server_url added successfully", serverConfig.getString("server_url"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_tag", "server_tag added successfully", serverConfig.getString("server_tag"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_login", "server_login added successfully", serverConfig.getString("server_login"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_password", "server_password added successfully", "***", KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_itemtype", "server_itemtype added successfully", serverConfig.getString("server_itemtype"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_custom_asset_serial", "server_custom_asset_serial added successfully", serverConfig.getString("server_custom_asset_serial"), KeyedAppState.SEVERITY_INFO);
                        AgentLog.e("EMM - Server added successfully");
                    } else {
                        preferences.deletePreferences(serverConfig.getString("server_url"));
                        preferences.saveJSONObject(serverConfig.getString("server_url"), jsonServerConfig);
                        enterpriseFeedback(getApplicationContext(), "server_url", "server_url updated successfully", serverConfig.getString("server_url"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_tag", "server_tag updated successfully", serverConfig.getString("server_tag"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_login", "server_login updated successfully", serverConfig.getString("server_login"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_password", "server_password updated successfully", "***", KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_itemtype", "server_itemtype updated successfully", serverConfig.getString("server_itemtype"), KeyedAppState.SEVERITY_INFO);
                        enterpriseFeedback(getApplicationContext(), "server_custom_asset_serial", "server_custom_asset_serial updated successfully", serverConfig.getString("server_custom_asset_serial"), KeyedAppState.SEVERITY_INFO);
                        AgentLog.e("EMM - Server updated successfully");
                    }

                } catch (JSONException e) {
                    enterpriseFeedback(getApplicationContext(), "server_url", "error while adding/updating server -> " + e.getMessage(), serverConfig.getString("server_url"), KeyedAppState.SEVERITY_ERROR);
                    AgentLog.e("EMM - error while adding/updating server");
                    AgentLog.e("EMM - " + e.getMessage());
                }
            }
        } else {
            AgentLog.e("EMM - 'server_configuration_list' key is empty");
        }
        AgentLog.e("EMM - END resolve restrictions");
    }

    public void scheduleJob() {
        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String timeInventory = customSharedPreference.getString("timeInventory", "Week");

        final JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final ComponentName name = new ComponentName(this, InventoryJobScheduler.class);
        final JobInfo jobInfo = getJobInfo(InventoryJobScheduler.INVENTORY_JOB_ID, timeInventory, name);

        if(customSharedPreference.getBoolean("autoStartInventory", false)) {
            if (!isAlreadyScheduled(jobScheduler, jobInfo)) {
                if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
                    AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Scheduled job successfully!");
                }
            } else {
                AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Scheduled job already initialized");
            }
        } else {
            AgentLog.d("GLPI-AGENT-JOBSCHEDULER : autoStartInventory disabled, cancel JobScheduler");
            if (isAlreadyScheduled(jobScheduler, jobInfo)) {
                jobScheduler.cancel(InventoryJobScheduler.INVENTORY_JOB_ID);
            }
        }
    }

    private boolean isAlreadyScheduled(JobScheduler jobScheduler, JobInfo jobInfo) {
        boolean jobAlreadyScheduled = false;
        for (JobInfo existingJob: jobScheduler.getAllPendingJobs()) {
            // check if scheduler exist with same ID and same minimum latency
            if (existingJob.getId() == jobInfo.getId()
                    && existingJob.getMinLatencyMillis() == jobInfo.getMinLatencyMillis()) {
                jobAlreadyScheduled = true;
                break;
            }
        }
        return jobAlreadyScheduled;
    }

    private JobInfo getJobInfo(final int id, final String timeInventory, final ComponentName name) {

        //default week
        long interval = TimeUnit.DAYS.toMillis(7);
        if (timeInventory.equals("Day")) {
            interval = TimeUnit.DAYS.toMillis(1);
        } else if (timeInventory.equals("Week")) {
            interval = TimeUnit.DAYS.toMillis(7);
        } else if (timeInventory.equals("Month")) {
            interval = TimeUnit.DAYS.toMillis(30);
        }

        AgentLog.d("GLPI-AGENT-JOBSCHEDULER : Alarm scheduled each " + timeInventory);

        JobInfo.Builder builder = new JobInfo.Builder(id, name)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(interval);
        } else {
            builder.setPeriodic(interval);
        }

        return builder.build();
    }
}
