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
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.flyve.inventory.agent.utils.FlyveLog;

public class Accueil extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private Messenger mAgentService = null;
    private  String[] statusAgent = null;
    private boolean isAgentOk = false;
    private String barcode = null;
    private boolean notif = false;
    private SharedPreferences customSharedPreference;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());

    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mAgentService = new Messenger(service);

            try {
                Message msg = Message.obtain();
                msg.replyTo = mMessenger;
                msg.what = Agent.MSG_CLIENT_REGISTER;
                mAgentService.send(msg);

                mAgentService.send(Message.obtain(null, Agent.MSG_AGENT_STATUS));
            } catch (RemoteException e) {
                FlyveLog.e(e.getMessage());
            }

            if (notif){
                Toast.makeText(Accueil.this, R.string.agent_connected, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAgentOk = false;
            mAgentService = null;

            if (notif){
                Toast.makeText(Accueil.this, R.string.agent_disconnected, Toast.LENGTH_SHORT).show();
            }
        }
    };

    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).

        ComponentName result = startService(new Intent("org.flyve.loadInventory.agent"));
        if (result != null) {
            FlyveLog.log(this, " Agent started ", Log.INFO);
        } else {
            FlyveLog.log(this, " Agent already started ", Log.ERROR);
        }

        boolean mIsBound = bindService(new Intent(Accueil.this, Agent.class), mConnection, Context.BIND_NOT_FOREGROUND);

        if (mIsBound) {
            FlyveLog.log(this, "Connected successfully to Agent service", Log.INFO);
        } else {
            FlyveLog.log(this, "Failed to connect to Agent service", Log.ERROR);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusAgent = getResources().getStringArray(R.array.agent_status);

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

                stopService(new Intent("org.flyve.loadInventory.agent"));
                startService(new Intent("org.flyve.loadInventory.agent"));

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

                stopService(new Intent("org.flyve.loadInventory.agent"));
                startService(new Intent("org.flyve.loadInventory.agent"));

                return true;
            }

        });

        Preference runInventory = findPreference("runInventory");
        runInventory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Message msg;

                if (isAgentOk) {
                    try {
                        Toast.makeText(Accueil.this, R.string.inventory_started, Toast.LENGTH_LONG).show();

                        isAgentOk = false;

                        msg = Message.obtain(null, Agent.MSG_INVENTORY_START);
                        msg.replyTo = mMessenger;
                        if (barcode != null) {
                            Bundle b = new Bundle();
                            b.putString("BARCODE", barcode);
                            msg.setData(b);
                        }
                        mAgentService.send(msg);
                    } catch (RemoteException e) {
                        FlyveLog.e(e.getMessage());
                    }

                    isAgentOk = true;
                }

                if (isAgentOk) {

                    isAgentOk = false;

                    msg = Message.obtain(null, Agent.MSG_INVENTORY_SEND);
                    msg.replyTo = mMessenger;
                    try {
                        mAgentService.send(msg);
                    } catch (RemoteException e) {
                        FlyveLog.e(e.getMessage());
                    }

                    isAgentOk = true;
                }

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

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            FlyveLog.log(this, " message received " + msg.toString(), Log.INFO);

            switch (msg.what) {
                case Agent.MSG_AGENT_STATUS:

                    FlyveLog.log(this, statusAgent[msg.arg1], Log.INFO);
                    isAgentOk = msg.arg1 == 0;
                    break;
                case Agent.MSG_INVENTORY_FINISHED:
                    try {
                        mAgentService.send(Message.obtain(null, Agent.MSG_INVENTORY_RESULT));
                    } catch (RemoteException e) {
                        FlyveLog.e(e.getMessage());
                    }
                    break;

                case Agent.MSG_INVENTORY_RESULT:
                    Bundle bXML = msg.peekData();
                    if (bXML != null) {
                        try {
                            mAgentService.send(Message.obtain(null, Agent.MSG_AGENT_STATUS));
                        } catch (RemoteException e) {
                            FlyveLog.e(e.getMessage());
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
