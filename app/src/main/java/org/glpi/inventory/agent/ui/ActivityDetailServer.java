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

package org.glpi.inventory.agent.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.flyve.inventory.InventoryLog;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.detailserver.DetailServer;
import org.glpi.inventory.agent.core.detailserver.DetailServerPresenter;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.Utils;
import org.json.JSONObject;

import android.net.Uri;
import java.util.ArrayList;

import static android.view.View.GONE;

public class ActivityDetailServer extends AppCompatActivity implements DetailServer.View, View.OnClickListener {

    private DetailServer.Presenter presenter;
    private Button deleteServer;
    private Button actionServer;
    private EditText editUrlAddress;
    private EditText editTag;
    private EditText editLogin;
    private EditText editPassWord;
    private Toolbar toolbar;
    private String serverName;
    private JSONObject extra_Data = null;
    private FloatingActionButton btnScan;

    private static final int REQUEST_CODE_SCAN = 150;

    /**
     * Called when the activity is starting, inflates the activity's UI
     *
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_server);
        presenter = new DetailServerPresenter(this);

        instanceObject();
        setToolbar();

        serverName = getIntent().getStringExtra("serverName");
        if (serverName == null) {
            actionServer.setText(R.string.add_server);
            deleteServer.setVisibility(GONE);
        } else {
            actionServer.setText(R.string.update_server);
            presenter.loadServer(serverName, getApplicationContext());
        }

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityDetailServer.this.startActivityForResult(new Intent(ActivityDetailServer.this, ScanActivity.class), REQUEST_CODE_SCAN);
            }
        });


        //try to know if com from deeplink
        String deeplink = "";
        Intent intent = getIntent();

        if(intent != null){

            Uri deeplink_Data = null;
            deeplink_Data = intent.getData();

            if(deeplink_Data != null){
                try {
                    deeplink = deeplink_Data.getQueryParameter("data");
                    //decode base64
                    byte[] decodedBytes = Base64.decode(deeplink,Base64.DEFAULT);
                    String decodedString = new String(decodedBytes);

                    // come from QR scan
                    try {
                        extra_Data = new JSONObject(decodedString);
                    } catch (Exception e) {
                        Toast.makeText(this, getApplicationContext().getResources().getString(R.string.bad_deeplink_format), Toast.LENGTH_LONG).show();
                        AgentLog.e(getApplicationContext().getResources().getString(R.string.bad_deeplink_format)+ " " + decodedString);
                    }

                    if(extra_Data != null){
                        try {
                            editUrlAddress.setText(extra_Data.getString("URL"));
                            editTag.setText(extra_Data.getString("TAG"));
                            editLogin.setText(extra_Data.getString("LOGIN"));
                            editPassWord.setText(extra_Data.getString("PASSWORD"));
                        } catch (Exception ex) {
                            Toast.makeText(this, getApplicationContext().getResources().getString(R.string.bad_deeplink_format), Toast.LENGTH_LONG).show();
                            AgentLog.e(getApplicationContext().getResources().getString(R.string.bad_deeplink_format));
                        }
                    }

                } catch (Exception ex) {
                    AgentLog.e(getApplicationContext().getResources().getString(R.string.error_deeplink)+ " " + ex);
                }
            }
        }

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            String input = intent.getStringExtra(ScanActivity.INTENT_EXTRA_RESULT);

            //decode base64
            byte[] decodedBytes = Base64.decode(input,Base64.DEFAULT);
            String decodedString = new String(decodedBytes);

            // come from QR scan

            try {
                extra_Data = new JSONObject(decodedString);
            } catch (Exception e) {
                Toast.makeText(this, getApplicationContext().getResources().getString(R.string.bad_qr_code_format), Toast.LENGTH_LONG).show();
                AgentLog.e(getApplicationContext().getResources().getString(R.string.bad_qr_code_format)+ " " + decodedString);
            }

            if(extra_Data != null){
                try {
                    editUrlAddress.setText(extra_Data.getString("URL"));
                    editTag.setText(extra_Data.getString("TAG"));
                    editLogin.setText(extra_Data.getString("LOGIN"));
                    editPassWord.setText(extra_Data.getString("PASSWORD"));
                } catch (Exception ex) {
                    Toast.makeText(this, getApplicationContext().getResources().getString(R.string.bad_qr_code_format), Toast.LENGTH_LONG).show();
                    AgentLog.e(getApplicationContext().getResources().getString(R.string.bad_qr_code_format));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void setToolbar() {
        try {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
        }
    }

    private void instanceObject() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.getContext().setTheme(R.style.toolbarArrow);
        deleteServer = toolbar.findViewById(R.id.deleteServer);
        actionServer = findViewById(R.id.actionServer);
        editUrlAddress = findViewById(R.id.editUrlAddress);
        editTag = findViewById(R.id.editTag);
        editLogin = findViewById(R.id.editLogin);
        editPassWord = findViewById(R.id.editPassWord);
        actionServer.setOnClickListener(this);
        deleteServer.setOnClickListener(this);
        btnScan = findViewById(R.id.btnQRScan);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionServer:
                ArrayList<String> serverInfo = new ArrayList<>();
                serverInfo.add(editUrlAddress.getText().toString());
                serverInfo.add(editTag.getText().toString());
                serverInfo.add(editLogin.getText().toString());
                serverInfo.add(editPassWord.getText().toString());
                if (serverName == null) {
                    presenter.saveServer(serverInfo, getApplicationContext());
                    //manage automatic inventory
                    if(extra_Data != null){
                        try {
                            if(extra_Data.getString("ANDROID_AUTOMATIC_INVENTORY").equalsIgnoreCase("1")){
                                SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = customSharedPreference.edit();
                                editor.putString("timeInventory", extra_Data.getString("ANDROID_FREQUENCY"));

                                //send broadcast message
                                Intent intent = new Intent("timeAlarmChanged");
                                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                                editor.putBoolean("autoStartInventory", true);
                                editor.apply();
                            }

                            if(extra_Data.has("ANDROID_START_ON_BOOT") &&
                                    extra_Data.getString("ANDROID_START_ON_BOOT").equalsIgnoreCase("1")){
                                SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = customSharedPreference.edit();
                                editor.putBoolean("boot", true);
                                editor.apply();
                            }
                        } catch (Exception ex) {
                            AgentLog.e(getApplicationContext().getResources().getString(R.string.bad_qr_code_format));
                        }
                    }
                } else {
                    presenter.updateServer(serverInfo, serverName, getApplicationContext());
                }
                break;
            case R.id.deleteServer:
                String message = "You want delete this server";
                Utils.showAlertDialog(message, this, new Utils.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess() {
                        presenter.deleteServer(serverName, getApplicationContext());
                    }
                });
                break;
        }
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityDetailServer.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void successful(String message) {
        onBackPressed();
    }

    @Override
    public void modelServer(ServerSchema model) {
        editUrlAddress.setText(model.getAddress());
        editTag.setText(model.getTag());
        editLogin.setText(model.getLogin());
        editPassWord.setText(model.getPass());
        btnScan.hide();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("reload-servers"));
    }
}
