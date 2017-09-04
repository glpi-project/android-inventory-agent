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
package org.flyve.inventory.agent.utils;

import android.util.Log;

/**
 * This is a Log wrapper
 */
public class FlyveLog {

    private static final String TAG = "InventoryAgent";

    /**
     * private constructor to prevent instances of this class
     */
    private FlyveLog() {
    }

    /**
     * Sends a DEBUG log message
     * @param string the message to log
     */
    public static void d(String message) {
        if(message != null) {
            Log.d(TAG, message);
        }
    }

    /**
     * Sends a VERBOSE log message
     * @param string the message to log
     */
    public static void v(String message) {
        if(message != null) {
            Log.v(TAG, message);
        }
    }

    /**
     * Sends an INFO log message
     * @param string the message to log
     */
    public static void i(String message) {
        if(message != null) {
            Log.i(TAG, message);
        }
    }

    /**
     * Sends an ERROR log message
     * @param string the message to log
     */
    public static void e(String message) {
        if(message != null) {
            Log.e(TAG, message);
        }
    }

    /**
     * Sends a WARN log message
     * @param string the message to log
     */
    public static void w(String message) {
        if(message != null) {
            Log.w(TAG, message);
        }
    }

    /**
     * Reports a condition that should never happen, wts (What a Terrible Failure)
     * @param string the message to log
     */
    public static void wtf(String message) {
        if(message != null) {
            Log.wtf(TAG, message);
        }
    }

    public static void log(Object obj, String msg, int level) {
        String final_msg = String.format("[%s] %s", obj.getClass().getName(), msg);
        Log.println(level, "InventoryAgent", final_msg);
    }
}
