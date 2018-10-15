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
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.utils;

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

    public ArrayList<String> loadServerArray() {
        ArrayList<String> ids = new ArrayList<>();
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        int size = Integer.parseInt(mSharedPreference1.getString("Status_size", ""));

        for (int i = 0; i < size; i++) {
            ids.add(mSharedPreference1.getString("Status_" + i, ""));
        }
        return ids;
    }

    public void saveServerArray(ArrayList<String> list) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putString("Status_size", String.valueOf(list.size()));

        for (int i = 0; i < list.size(); i++) {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, list.get(i));
        }
        mEdit1.apply();
    }
}
