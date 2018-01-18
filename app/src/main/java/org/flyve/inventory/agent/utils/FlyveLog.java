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
 * @link      https://github.com/flyvemdm/android-inventory-agent
 * @link      http://www.glpi-project.org/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;

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
     * @param message to log
     */
    public static void d(String message) {
        if(message != null) {
            Logger.d(TAG, message);
        }
    }

    /**
     * Sends a VERBOSE log message
     * @param message to log
     */
    public static void v(String message) {
        if(message != null) {
            Logger.v(TAG, message);
        }
    }

    /**
     * Sends an INFO log message
     * @param message to log
     */
    public static void i(String message) {
        if(message != null) {
            Logger.i(TAG, message);
        }
    }

    /**
     * Sends an ERROR log message
     * @param message to log
     */
    public static void e(String message) {
        if(message != null) {
            Logger.e(TAG, message);
        }
    }

    /**
     * Sends a WARN log message
     * @param message to log
     */
    public static void w(String message) {
        if(message != null) {
            Logger.w(TAG, message);
        }
    }

    /**
     * Reports a condition that should never happen, wts (What a Terrible Failure)
     * @param message to log
     */
    public static void wtf(String message) {
        if(message != null) {
            Logger.wtf(TAG, message);
        }
    }

    /**
     * Sends a low level calling log
     * @param obj the name of the class
     * @param msg the log message
     * @param level the priority/type of the log message
     */
    public static void log(Object obj, String msg, int level) {
        String final_msg = String.format("[%s] %s", obj.getClass().getName(), msg);
        Log.println(level, "InventoryAgent", final_msg);
    }
}
