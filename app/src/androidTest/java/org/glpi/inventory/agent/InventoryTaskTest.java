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

package org.glpi.inventory.agent;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.HttpInventory;
import org.glpi.inventory.agent.utils.UtilsAgent;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InventoryTaskTest {

    private Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void getJSON() throws Exception {
        InventoryTask task = new InventoryTask(appContext, "test", true);
        task.getJSON(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                assertNotEquals("", data);
            }

            @Override
            public void onTaskError(Throwable error) {
                assertTrue(true);
            }
        });
    }

    @Test
    public void getXML() throws Exception {
        InventoryTask task = new InventoryTask(appContext, "test", true);
        task.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                assertNotEquals("", data);
            }

            @Override
            public void onTaskError(Throwable error) {
                assertTrue(true);
            }
        });
    }

    @Test
    public void sendInventoryTest() throws Exception {
        // Tested XML with good format
        String data = UtilsAgent.getXml("inventory.xml", appContext);
        // Send xml to default route
        HttpInventory httpInventory = new HttpInventory(appContext);
        ServerSchema serverSchema = new ServerSchema();
        serverSchema.setAddress("https://demo-api.flyve.org/plugins/fusioninventory/");
        serverSchema.setTag("");
        serverSchema.setLogin("");
        serverSchema.setPass("");
        httpInventory.sendInventory(data, serverSchema, new HttpInventory.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                AgentLog.d(data);
                assertTrue(true);
            }

            @Override
            public void onTaskError(String error) {
                AgentLog.e(error);
                assertTrue(false);
            }
        });
    }

}
