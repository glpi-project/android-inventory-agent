/**
 * ---------------------------------------------------------------------
 * GLPI Android Inventory Agent
 * Copyright (C) 2019 Teclib.
 *
 * https://glpi-project.org
 *
 * Based on Flyve MDM Inventory Agent For Android
 * Copyright © 2018 Teclib. All rights reserved.
 *
 * ---------------------------------------------------------------------
 *
 *  LICENSE
 *
 *  This file is part of GLPI Android Inventory Agent.
 *
 *  GLPI Android Inventory Agent is a subproject of GLPI.
 *
 *  GLPI Android Inventory Agent is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  GLPI Android Inventory Agent is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @copyright Copyright © 2019 Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/glpi-project/android-inventory-agent
 *  @link      https://glpi-project.org/glpi-network/
 *  ---------------------------------------------------------------------
 */


package org.glpi.inventory.agent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {

	private static final String SHARED_PREFS_FILE = "FlyveInventoryAgentData";
	private Context mContext;

	/**
	 * Constructor
	 * @param context
	 */
	public LocalStorage(Context context){
		mContext = context;
	}

	/**
	 * Get preference from setting
	 * @return SharedPreferences
	 */
	private SharedPreferences getSettings(){
		if (mContext != null) {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
		} else {
			return null;
		}
	}

	/**
	 * Get the data matching the given argument
	 * @param key
	 * @return string the data
	 */
	public String getData(String key){
		String data = "";
		SharedPreferences sp = getSettings();
		if(sp != null) {
			data = sp.getString(key, null);
			if(data == null) {
				data = "";
			}
		}
		return data;
	}

	/**
	 * Set the data given in the argument to the Shared Preferences
	 * @param key
	 * @param value
	 */
	public void setData(String key, String value) {
		SharedPreferences sp = getSettings();
		if(sp != null) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, value );
			editor.apply();
		}
	}

	public void setDataBoolean(String key, Boolean value) {
		SharedPreferences sp = getSettings();
		if(sp != null) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(key, value);
			editor.apply();
		}
	}

	public Boolean getDataBoolean(String key){
		Boolean data = false;
		SharedPreferences sp = getSettings();

		if(sp != null) {
			data = sp.getBoolean(key, false);
		}

		return data;
	}

	public void setDataLong(String key, long value) {
		SharedPreferences sp = getSettings();
		if(sp != null) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putLong(key, value);
			editor.apply();
		}
	}

	public Long getDataLong(String key){
		Long data = null;
		SharedPreferences sp = getSettings();

		if(sp != null) {
			data = sp.getLong(key, 0);
		}

		return data;
	}


	/**
	 * Remove all the values from the preferences
	 */
	public void clearSettings(){
		SharedPreferences sp = getSettings();
		if(sp != null) {
			SharedPreferences.Editor editor = sp.edit();
			editor.clear();
			editor.apply();
		}
	}

	/**
	 * Remove the key cache
	 * @param key value to remove
	 */
	public void deleteKeyCache(String key){
		SharedPreferences sp = getSettings();
		if(sp != null) {
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.apply();
		}
	}
}
