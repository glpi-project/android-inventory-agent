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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.adapter.InventoryAdapter;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView lst;
    private ProgressBar pb;

    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);

        FloatingActionButton btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogShare();
            }
        });

        lst = findViewById(R.id.lst);

        GridLayoutManager llm = new GridLayoutManager(InventoryActivity.this, 1);
        lst.setLayoutManager(llm);

        final InventoryTask inventoryTask = new InventoryTask(InventoryActivity.this, Helpers.getAgentDescription(InventoryActivity.this), true);
        inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(final String s) {
                load(s);
                inventoryTask.getXMLSyn();
            }

            @Override
            public void onTaskError(Throwable throwable) {
                pb.setVisibility(View.GONE);
                FlyveLog.e(throwable.getMessage());
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

                    if(!key.trim().equals("")) {
                        data.add(h);
                    }

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
            pb.setVisibility(View.GONE);

            InventoryAdapter mAdapter = new InventoryAdapter(InventoryActivity.this, data);
            lst.setAdapter(mAdapter);

        } catch (Exception ex) {
            pb.setVisibility(View.GONE);
            FlyveLog.e(ex.getMessage());
        }
    }

    public void showDialogShare() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InventoryActivity.this);
        builder.setTitle(R.string.dialog_share_title);

        final int[] type = new int[1];

        //list of items
        String[] items = getResources().getStringArray(R.array.export_list);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        type[0] = which;
                    }
                });

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        Helpers.share( InventoryActivity.this, "Inventory Agent File", type[0] );
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
