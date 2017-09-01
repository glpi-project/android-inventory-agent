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

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.flyvemdm.inventory.InventoryTask;

import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.HttpInventory;

public class Accueil extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private boolean notif = false;
    private SharedPreferences customSharedPreference;

    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
    }


    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).

        ComponentName result = startService(new Intent("org.flyve.inventory.agent"));
        if (result != null) {
            FlyveLog.log(this, " Agent started ", Log.INFO);
        } else {
            FlyveLog.log(this, " Agent already started ", Log.ERROR);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.accueil);

        doBindService();

        customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        notif = customSharedPreference.getBoolean("notif", false);

        Preference autoStartInventory = findPreference("autoStartInventory");
        autoStartInventory.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {

                notif = customSharedPreference.getBoolean("notif", false);

                if (notif){
                    Toast.makeText(getBaseContext(), R.string.agent_reboot, Toast.LENGTH_SHORT).show();
                }

                stopService(new Intent("org.flyve.inventory.agent"));
                startService(new Intent("org.flyve.inventory.agent"));

                return true;
            }

        });

        Preference timeInventory = findPreference("timeInventory");
        timeInventory.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference arg0, Object arg1) {

                notif = customSharedPreference.getBoolean("notif", false);

                if (notif){
                    Toast.makeText(getBaseContext(), R.string.agent_reboot,Toast.LENGTH_SHORT).show();
                }

                stopService(new Intent("org.flyve.inventory.agent"));
                startService(new Intent("org.flyve.inventory.agent"));

                return true;
            }

        });

        Preference runInventory = findPreference("runInventory");
        runInventory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                InventoryTask inventoryTask = new InventoryTask(Accueil.this, "");
                inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        FlyveLog.d(data);
                        HttpInventory httpInventory = new HttpInventory(Accueil.this);
                        httpInventory.sendInventory( data );
                        Toast.makeText(Accueil.this, "Inventory send", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTaskError(Throwable error) {
                        Toast.makeText(Accueil.this, "Inventory fail, please try again", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            }

        });
    }

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
