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

import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * This is a Log wrapper
 */
public class AgentLog {

    private static final String TAG = "InventoryAgent";

    /**
     * private constructor to prevent instances of this class
     */
    private AgentLog() {
    }

    /**
     * Sends a DEBUG log message
     * @param message to log
     */
    public static void d(String message, Object... args) {
        if(message != null) {
            Logger.d(message, args);
        }
    }

    /**
     * Sends a VERBOSE log message
     * @param message to log
     */
    public static void v(String message, Object... args) {
        if(message != null) {
            Logger.v(message, args);
        }
    }

    /**
     * Sends an INFO log message
     * @param message to log
     */
    public static void i(String message, Object... args) {
        if(message != null) {
            Logger.i(message, args);
        }
    }

    /**
     * Sends an ERROR log message
     * @param message to log
     */
    public static void e(String message, Object... args) {
        if(message != null) {
            Logger.e(message, args);
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
