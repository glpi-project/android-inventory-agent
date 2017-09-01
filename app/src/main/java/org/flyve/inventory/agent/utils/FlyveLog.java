/*
 * Copyright (C) 2016 Teclib'
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

    public static void d(String message) {
        if(message != null) {
            Log.d(TAG, message);
        }
    }

    public static void v(String message) {
        if(message != null) {
            Log.v(TAG, message);
        }
    }

    public static void i(String message) {
        if(message != null) {
            Log.i(TAG, message);
        }
    }

    public static void e(String message) {
        if(message != null) {
            Log.e(TAG, message);
        }
    }

    public static void w(String message) {
        if(message != null) {
            Log.w(TAG, message);
        }
    }

    public static void wtf(String message) {
        if(message != null) {
            Log.wtf(TAG, message);
        }
    }
}
