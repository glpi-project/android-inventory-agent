/**
 *  LICENSE
 *
 *  This file is part of Flyve MDM Inventory Agent for Android.
 * 
 *  Inventory Agent for Android is a subproject of Flyve MDM.
 *  Flyve MDM is a mobile device management software.
 *
 *  Flyve MDM is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  Flyve MDM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @copyright Copyright © 2018 Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/flyve-mdm/android-inventory-agent
 *  @link      https://flyve-mdm.com
 *  @link      http://flyve.org/android-inventory-agent
 *  ---------------------------------------------------------------------
 */

package org.glpi.inventory.agent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocalPreferences {

    private Context mContext;

    public LocalPreferences(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Get setting’s preferences
     *
     * @return SharedPreferences data type
     */
    private SharedPreferences getSettings() {
        String SHARED_PREFS_FILE = "HMPrefs";
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }

    /**
     * Store an Json Array
     */
    public void saveJSONObject(String key, JSONObject object) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, object.toString());
        editor.apply();
    }

    public void deletePreferences(String key) {
        SharedPreferences preferences = getSettings();
        preferences.edit().remove(key).apply();
    }

    /**
     * @return an Json Object
     */
    public JSONObject loadJSONObject(String key) throws JSONException {
        return new JSONObject(getSettings().getString(key, "{}"));
    }

    public ArrayList<String> loadCategories() {
        return loadArray("Status_size_categories", "Status_categories_");
    }

    public void saveCategories(ArrayList<String> list) {
        saveArray(list, "Status_size_categories", "Status_categories_");
    }

    public ArrayList<String> loadServer() {
        return loadArray("Status_size", "Status_");
    }

    public void saveServer(ArrayList<String> list) {
        saveArray(list, "Status_size", "Status_");
    }

    private ArrayList<String> loadArray(String status_size, String status_) {
        ArrayList<String> ids = new ArrayList<>();
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        int size = Integer.parseInt(mSharedPreference1.getString(status_size, "0"));

        for (int i = 0; i < size; i++) {
             ids.add(mSharedPreference1.getString(status_ + i, ""));
        }
        return ids;
    }

    private void saveArray(ArrayList<String> list, String status_size, String status_) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putString(status_size, String.valueOf(list.size()));

        for (int i = 0; i < list.size(); i++) {
            mEdit1.remove(status_ + i);
            mEdit1.putString(status_ + i, list.get(i));
        }
        mEdit1.apply();
    }
}
