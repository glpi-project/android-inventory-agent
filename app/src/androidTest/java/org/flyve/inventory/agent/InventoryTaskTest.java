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
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.schema.ServerSchema;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.HttpInventory;
import org.flyve.inventory.agent.utils.UtilsAgent;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InventoryTaskTest {

    private Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void getJSON() throws Exception {
        InventoryTask task = new InventoryTask(appContext, "test");
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
        InventoryTask task = new InventoryTask(appContext, "test");
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
                FlyveLog.d(data);
                assertTrue(true);
            }

            @Override
            public void onTaskError(String error) {
                FlyveLog.e(error);
                assertTrue(false);
            }
        });
    }

}
