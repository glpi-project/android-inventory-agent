package org.fusioninventory;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

public class FusionInventoryApp
        extends Application
        implements OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    public Boolean mShouldAutoStart = null;
    public String mUrl = null;
    public String mTag = null;
    public String mLogin = null;
    public String mPassword = null;
    public String mDeviceID = null;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        //prefs.edit().remove("device_id").commit();
        
        String device_id = prefs.getString("device_id", null);
        
        FusionInventory.log(this, device_id, Log.VERBOSE);
        
             
        if(device_id == null) {
           
           TelephonyManager mTM= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
           prefs.edit()
                .putString("device_id", String.format("%s-%s",mTM.getDeviceId(),DateFormat.format("yyyy-MM-dd-kk-mm-ss", System.currentTimeMillis())))
                .commit();
        }
        
        
    }

    
    public String getDeviceID(){
        if (mDeviceID == null) {
            mDeviceID = prefs.getString("device_id","<not set>");
        }
        return mDeviceID;
    }
    
    public Boolean getShouldAutoStart() {

        if (mShouldAutoStart == null) {

            mShouldAutoStart = Boolean.valueOf(prefs.getBoolean("boot", false));

        }
        return mShouldAutoStart;

    }

    public String getUrl() {
        if (mUrl == null) {
            mUrl = prefs.getString("url", "");
        }
        return mUrl;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO Auto-generated method stub
        
        mUrl = null;
        mShouldAutoStart = null;
        FusionInventory.log(this, "FusionInventoryApp = " + this.toString(), Log.VERBOSE);
    }

    public String getCredentialsLogin() {
        if (mLogin == null) {
        	mLogin = prefs.getString("login", "");
        }
        return mLogin;
    }

    public String getCredentialsPassword() {
        if (mPassword == null) {
        	mPassword = prefs.getString("password", "");
        }
        return mPassword;
    }

    public String getTag() {
        if (mTag == null) {
        	mTag = prefs.getString("tag", "");
        }
        return mTag;
    }

}
