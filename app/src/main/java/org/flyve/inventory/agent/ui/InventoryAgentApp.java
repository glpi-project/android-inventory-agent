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
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.UtilsCrash;

public class InventoryAgentApp extends Application implements OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;

    private Boolean mShouldAutoStart = null;
    private String mUrl = null;
    private String mDeviceID = null;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * This method is called when the application is starting, it gets the default Shared Preferences
     */
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean val = sharedPreferences.getBoolean("crashReport",false);

        UtilsCrash.configCrash(this, val);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        String deviceId = prefs.getString("device_id", null);

        FlyveLog.log(this, deviceId, Log.VERBOSE);

        try {
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .tag(getPackageName())   // (Optional) Global tag for every log.
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    /**
     * Gets the ID of the device
     * @return string the device ID
     */
    public String getDeviceID(){
        if (mDeviceID == null) {
            mDeviceID = prefs.getString("device_id","<not set>");
        }
        return mDeviceID;
    }
    
    /**
     * Gets if it should auto start
     * @return boolean true if it should, false otherwise
     */
    public Boolean getShouldAutoStart() {
        if (mShouldAutoStart == null) {
            mShouldAutoStart = prefs.getBoolean("boot", false);
        }
        return mShouldAutoStart;

    }

    /**
     * Gets the URL
     * @return string the URL 
     */
    public String getUrl() {
        // This variable is preset for instrumented test
        mUrl = prefs.getString("url","https://demo-api.flyve.org/plugins/fusioninventory/");
        return mUrl;
    }

    /**
     * Called when a shared preference is changed, added, or removed
     * @param sharedPreferences that received the change
     * @param key of the preference that was changed, added or removed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mUrl = null;
        mShouldAutoStart = null;
        FlyveLog.log(this, "InventoryAgentApp = " + this.toString(), Log.VERBOSE);
    }

    /**
     * Gets the credentials login
     * @return string the login
     */
    public String getCredentialsLogin() {
        return prefs.getString("login", "");
    }

    /**
     * Gets the credentials password
     * @return string the password
     */
    public String getCredentialsPassword() {
        return prefs.getString("password", "");
    }

}
