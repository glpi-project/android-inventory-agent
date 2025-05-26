/**
 * ---------------------------------------------------------------------
 * GLPI Android Inventory Agent
 * Copyright (C) 2019 Teclib.
 * <p>
 * https://glpi-project.org
 * <p>
 * Based on Flyve MDM Inventory Agent For Android
 * Copyright © 2018 Teclib. All rights reserved.
 * <p>
 * ---------------------------------------------------------------------
 * <p>
 * LICENSE
 * <p>
 * This file is part of GLPI Android Inventory Agent.
 * <p>
 * GLPI Android Inventory Agent is a subproject of GLPI.
 * <p>
 * GLPI Android Inventory Agent is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GLPI Android Inventory Agent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ---------------------------------------------------------------------
 *
 * @copyright Copyright © 2019 Teclib. All rights reserved.
 * @license GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link https://github.com/glpi-project/android-inventory-agent
 * @link https://glpi-project.org/glpi-network/
 * ---------------------------------------------------------------------
 */

package org.glpi.inventory.agent.ui;

import static org.glpi.inventory.agent.utils.Utils.showAlertDialog;
import static org.glpi.inventory.agent.utils.Utils.showInfoDialog;
import static org.glpi.inventory.agent.utils.XMLConfig.importServer;
import static kotlinx.coroutines.flow.FlowKt.skip;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Xml;
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
import org.glpi.inventory.agent.service.InventoryService;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.LocalPreferences;
import org.glpi.inventory.agent.utils.LocalStorage;
import org.glpi.inventory.agent.utils.Utils;
import org.glpi.inventory.agent.utils.XMLConfig;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private FloatingActionButton btn_config;
    private Boolean isOpen;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private TextView textview_settings, textview_scheduler, textview_config;

    final static int REQUESTCODE_OPEN_DOC = 2;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strTime = intent.getStringExtra("time");
            if (sharedPreferences.getBoolean("autoStartInventory", false)) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, new IntentFilter(InventoryService.TIMER_RECEIVER), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, new IntentFilter(InventoryService.TIMER_RECEIVER));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 34) {
            ActivityCompat.requestPermissions(ActivityMain.this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
                    },
                    1);
        } else {
            ActivityCompat.requestPermissions(ActivityMain.this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                    },
                    1);
        }
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
                if (!presenter.getMenuItem().get(position).get("id").equals("1")) {
                    if (isOpen) {
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
                this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        IntentFilter timeAlarmChanged = new IntentFilter("timeAlarmChanged");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, timeAlarmChanged);

        //FloatActionButton
        mainFab = findViewById(R.id.fab);
        btn_settings = findViewById(R.id.btn_settings);
        btn_scheduler = findViewById(R.id.btn_scheduler);
        btn_config = findViewById(R.id.btn_config);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_fab_anticlock);

        textview_settings = findViewById(R.id.text_settings);
        textview_scheduler = findViewById(R.id.text_scheduler);
        textview_config = findViewById(R.id.text_config);
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
                if (isOpen) {
                    openFab();
                }
                Intent miIntent = new Intent(ActivityMain.this, GlobalParametersPreference.class);
                ActivityMain.this.startActivity(miIntent);
            }
        });

        btn_scheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    openFab();
                }
                Intent miIntent = new Intent(ActivityMain.this, InventoryParametersPreference.class);
                ActivityMain.this.startActivity(miIntent);
            }
        });


        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    openFab();
                }
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/xml");
                startActivityForResult(intent, REQUESTCODE_OPEN_DOC);
            }
        });

        if (!XMLConfig.checkFilesAccess(this)) {
            this.finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager())
                XMLConfig.autoImportServer(this);
        }
        AgentLog.setLogFile();
        AgentLog.i("Application Start");
    }

    private void disableFab() {
        mainFab.hide();
        btn_settings.hide();
        btn_scheduler.hide();
        btn_config.hide();
    }

    public void enableFab() {
        if (mainFab.getVisibility() != View.VISIBLE) {
            mainFab.show();
        }
    }

    private void openFab() {
        if (isOpen) {
            textview_settings.setVisibility(View.INVISIBLE);
            textview_scheduler.setVisibility(View.INVISIBLE);
            textview_config.setVisibility(View.INVISIBLE);
            btn_settings.startAnimation(fab_close);
            btn_scheduler.startAnimation(fab_close);
            btn_config.startAnimation(fab_close);
            mainFab.startAnimation(fab_anticlock);
            btn_settings.setClickable(false);
            btn_scheduler.setClickable(false);
            btn_config.setClickable(false);
            isOpen = false;
        } else {
            textview_settings.setVisibility(View.VISIBLE);
            textview_scheduler.setVisibility(View.VISIBLE);
            textview_config.setVisibility(View.VISIBLE);
            btn_settings.startAnimation(fab_open);
            btn_scheduler.startAnimation(fab_open);
            btn_config.startAnimation(fab_open);
            mainFab.startAnimation(fab_clock);
            btn_settings.setClickable(true);
            btn_scheduler.setClickable(true);
            btn_config.setClickable(true);
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
        if (!isOpen) {
            super.onBackPressed();
        } else {
            openFab();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                boolean allGranted = true;
                for (int i = 0; i < permissions.length; i++) {
                    System.out.println("permission " + permissions[i] + " " + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        allGranted = false;

                    if (((Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i]))
                            || (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i]))
                    )
                            && (grantResults[i] == PackageManager.PERMISSION_GRANTED))
                        XMLConfig.autoImportServer(this);
                }

                // If request is cancelled, the result arrays are empty.
                if (!allGranted) {
                    String message = getResources().getString(R.string.permission_error_result);
                    Helpers.snackClose(ActivityMain.this, message, getString(R.string.permission_snack_ok), true);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        try {
            if (requestCode == REQUESTCODE_OPEN_DOC
                    && resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (resultData != null && resultData.getData() != null) {
                    uri = resultData.getData();
                    importServer(this, getContentResolver().openInputStream(uri));
                }
            }
        } catch (Exception e) {
            showInfoDialog("Erreur lors de l'import du fichier xml", this);
            AgentLog.e(e.getMessage());
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }


}
