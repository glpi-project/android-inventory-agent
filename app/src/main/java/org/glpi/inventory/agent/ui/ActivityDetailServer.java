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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.detailserver.DetailServer;
import org.glpi.inventory.agent.core.detailserver.DetailServerPresenter;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.Utils;

import java.util.ArrayList;

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
            deleteServer.setVisibility(View.GONE);
        } else {
            actionServer.setText(R.string.update_server);
            presenter.loadServer(serverName, getApplicationContext());
        }
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
        deleteServer = toolbar.findViewById(R.id.deleteServer);
        actionServer = findViewById(R.id.actionServer);
        editUrlAddress = findViewById(R.id.editUrlAddress);
        editTag = findViewById(R.id.editTag);
        editLogin = findViewById(R.id.editLogin);
        editPassWord = findViewById(R.id.editPassWord);
        actionServer.setOnClickListener(this);
        deleteServer.setOnClickListener(this);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("reload-servers"));
    }
}
