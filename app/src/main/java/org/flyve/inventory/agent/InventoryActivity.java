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
 * @link      https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent/
 * @link      http://www.glpi-project.org/
 * @link      https://flyve-mdm.com/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.adapter.InventoryAdapter;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView lst;
    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param Bundle savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.menu_about));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lst = (RecyclerView)findViewById(R.id.lst);

        GridLayoutManager llm = new GridLayoutManager(InventoryActivity.this, 1);
        lst.setLayoutManager(llm);

        InventoryTask inventoryTask = new InventoryTask(InventoryActivity.this, "");
        inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String s) {
                load(s);
            }

            @Override
            public void onTaskError(Throwable throwable) {

            }
        });
    }

    private void load(String jsonStr) {

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONObject jsonrequest = json.getJSONObject("request");
            JSONObject jsonContent = jsonrequest.getJSONObject("content");

                Iterator<?> keys = jsonContent.keys();

                while( keys.hasNext() ) {
                    String key = (String)keys.next();

                    if ( jsonContent.get(key) instanceof JSONArray ) {
                        // add header
                        FlyveLog.d("----------- Header: " + key);

                        HashMap<String, String> h = new HashMap<>();
                        h.put("type", "header");
                        h.put("title", key.toUpperCase());

                        data.add(h);

                        if(!key.equals("")) {
                            JSONArray category = jsonContent.getJSONArray(key);
                            for (int y = 0; y < category.length(); y++) {


                                JSONObject obj = category.getJSONObject(y);

                                Iterator<?> keysObj = obj.keys();

                                while (keysObj.hasNext()) {
                                    HashMap<String, String> c = new HashMap<>();

                                    String keyObj = (String) keysObj.next();
                                    c.put("type", "data");
                                    c.put("title", keyObj);
                                    c.put("description", obj.getString(keyObj));
                                    FlyveLog.d(keyObj);

                                    data.add(c);
                                }


                            }
                        }
                    }
                }

            InventoryAdapter mAdapter = new InventoryAdapter(InventoryActivity.this, data);
            lst.setAdapter(mAdapter);

        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

    }

}
