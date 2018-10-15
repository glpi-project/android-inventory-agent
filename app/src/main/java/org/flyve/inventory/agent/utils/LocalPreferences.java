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

    /**
     * @return an Json Object
     */
    public JSONObject loadJSONObject(String key) throws JSONException {
        return new JSONObject(getSettings().getString(key, "{}"));
    }

    public ArrayList<String> loadServerArray() {
        ArrayList<String> ids = new ArrayList<>();
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        int size = mSharedPreference1.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            ids.add(mSharedPreference1.getString("Status_" + i, ""));
        }
        return ids;
    }

    public boolean saveServerArray(ArrayList<String> list) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putString("Status_size", String.valueOf(list.size()));

        for (int i = 0; i < list.size(); i++) {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, list.get(i));
        }
        return mEdit1.commit();
    }
}
