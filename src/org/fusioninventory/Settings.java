package org.fusioninventory;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	// implements OnSharedPreferenceChangeListener {

	// SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		// prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// prefs.edit()

	}

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// }
	//
	//
	// @Override
	// protected void onResume(){
	// super.onResume();
	// // Set up a listener whenever a key changes
	// prefs.registerOnSharedPreferenceChangeListener(this);
	// }
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	//
	// // Unregister the listener whenever a key changes
	// prefs.unregisterOnSharedPreferenceChangeListener(this);
	// }
	//
	//
	// @Override
	// public void onSharedPreferenceChanged(SharedPreferences
	// sharedPreferences,
	// String key) {
	// // TODO Auto-generated method stub
	// FusionInventory.log(this, String.format("Pref %s changed: [%s]", key ,
	// sharedPreferences.getString(key, null)) , Log.WARN);
	//
	// }

}
