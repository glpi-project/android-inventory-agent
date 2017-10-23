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

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.utils.ConnectionHTTP;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;
import org.flyve.inventory.agent.utils.LocalStorage;
import org.flyve.inventory.agent.utils.UtilsCrash;

public class FragmentAccueil extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private Intent mServiceIntent;
    private Toolbar mToolbar;

    /**
     * Called when the activity will start interacting with the user
     * It registers a callback to be invoked when a change happens to a preference
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
    }

    /**
     * Called when the system is about to start resuming a previous activity
     * It unregisters a previous callback
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
    }

    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        InventoryService inventoryService = new InventoryService();
        mServiceIntent = new Intent(this, inventoryService.getClass());

        if (!isMyServiceRunning(inventoryService.getClass())) {
            ComponentName result = FragmentAccueil.this.startService(mServiceIntent);

            if (result != null) {
                FlyveLog.log(this, " Agent started ", Log.INFO);
            } else {
                FlyveLog.log(this, " Agent fail", Log.ERROR);
            }
        } else {
            FlyveLog.log(this, " Agent already started ", Log.ERROR);
        }
    }

    /**
     * Check if the service is running
     * @param serviceClass Class
     * @return boolean
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) FragmentAccueil.this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the activity is starting, adds the preference hierarchy to the current preference hierarchy
     * @param Bundle savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.accueil);

        doBindService();

        // When the AutoInventory is checked or unchecked, it stops the service and then starts it again
        Preference autoStartInventory = findPreference("autoStartInventory");
        autoStartInventory.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                FragmentAccueil.this.stopService( mServiceIntent );
                doBindService();
                return true;
            }

        });

        // When the frequency is changed, it stops the service and then starts it again 
        Preference timeInventory = findPreference("timeInventory");
        timeInventory.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                FragmentAccueil.this.stopService( mServiceIntent );
                doBindService();
                return true;
            }

        });

        Preference crashReport = findPreference("crashReport");
        crashReport.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FragmentAccueil.this);
                Boolean val = sharedPreferences.getBoolean("crashReport",false);
                UtilsCrash.configCrash(FragmentAccueil.this, val);

                LocalStorage localStorage = new LocalStorage(FragmentAccueil.this);
                localStorage.setData("crashReport", String.valueOf(val));

                FlyveLog.d("crashReport: " + val);
                return true;
            }
        });

        Preference anonymousData = findPreference("anonymousData");
        anonymousData.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FragmentAccueil.this);
                Boolean val = sharedPreferences.getBoolean("anonymousData",false);

                LocalStorage localStorage = new LocalStorage(FragmentAccueil.this);
                localStorage.setData("anonymousData", String.valueOf(val));

                FlyveLog.d("anonymousData: " + val);
                return true;
            }
        });

        // After the Inventory is run, it is sent
        Preference runInventory = findPreference("runInventory");
        runInventory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                final InventoryTask inventoryTask = new InventoryTask(FragmentAccueil.this, Helpers.getAgentDescription(FragmentAccueil.this));

                //((MainActivity)FragmentAccueil.this).loading(true);

                // Sending anonymous information
                inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        FlyveLog.d(data);
                        HttpInventory httpInventory = new HttpInventory(FragmentAccueil.this);
                        httpInventory.sendInventory(data, new HttpInventory.OnTaskCompleted() {
                            @Override
                            public void onTaskSuccess(String data) {
                                //((MainActivity)FragmentAccueil.this).loading(false);
                                Helpers.snackClose(FragmentAccueil.this, data, FragmentAccueil.this.getResources().getString(R.string.snackButton), false);
                                sendAnonymousData(inventoryTask);
                            }

                            @Override
                            public void onTaskError(String error) {
                                //((MainActivity)FragmentAccueil.this).loading(false);
                                Helpers.snackClose(FragmentAccueil.this, error, FragmentAccueil.this.getResources().getString(R.string.snackButton), true);

                            }
                        });
                    }

                    @Override
                    public void onTaskError(Throwable error) {
                        FlyveLog.e(error.getMessage());
                        //((MainActivity)FragmentAccueil.this).loading(false);
                        Helpers.snackClose(FragmentAccueil.this, error.getMessage(), FragmentAccueil.this.getResources().getString(R.string.snackButton), true);
                    }
                });

                return true;
            }

        });

        // Open about screen
        Preference showInventory = findPreference("showInventory");
        showInventory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent miIntent = new Intent(FragmentAccueil.this, InventoryActivity.class);
                FragmentAccueil.this.startActivity(miIntent);

                return true;
            }
        });

        // Open about screen
        Preference about = findPreference("About");
        about.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent miIntent = new Intent(FragmentAccueil.this, AboutActivity.class);
                FragmentAccueil.this.startActivity(miIntent);

                return true;
            }
        });

        // link to Help Center
        Preference helpCenter = findPreference("HelpCenter");
        helpCenter.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "http://flyve.org/flyve-mdm-android-inventory-agent/";
                openURL( FragmentAccueil.this, url );
                return true;
            }
        });

        //TODO waiting for help center to make it visible
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removePreference(helpCenter);
    }

    private void sendAnonymousData(InventoryTask inventoryTask) {
        // Sending anonymous information
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FragmentAccueil.this);
        Boolean val = sharedPreferences.getBoolean("anonymousData",false);
        if(val) {
            inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
                @Override
                public void onTaskSuccess(String s) {
                    FlyveLog.d(s);
                    ConnectionHTTP.syncWebData("https://inventory.chollima.pro/-1001061475099/", s);
                }

                @Override
                public void onTaskError(Throwable throwable) {
                    FlyveLog.e(throwable.getMessage());
                }
            });
        }


    }

    /**
     * Open url on browser
     * @param context Context where is working
     * @param url String the url to display
     */
    public static void openURL(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    /**
     * Called when a shared preference is changed, added, or removed
     * @param SharedPreferences the SharedPreferences that received the change
     * @param string the key of the preference that was changed, added or removed
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference editextp = (EditTextPreference) pref;
            pref.setSummary(editextp.getText());
        }
        if (pref instanceof ListPreference) {
            ListPreference listp = (ListPreference) pref;
            pref.setSummary(listp.getValue());
        }
    }
}
