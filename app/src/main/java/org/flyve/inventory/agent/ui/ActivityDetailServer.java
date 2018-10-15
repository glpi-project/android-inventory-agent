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
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.core.detailserver.DetailServer;
import org.flyve.inventory.agent.core.detailserver.DetailServerPresenter;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;

import java.util.ArrayList;

public class ActivityDetailServer extends AppCompatActivity implements DetailServer.View, View.OnClickListener {

    private DetailServer.Presenter presenter;
    private Button deleteServer;
    private EditText editUrlAddress;
    private EditText editTag;
    private EditText editLogin;
    private EditText editPassWord;
    private Toolbar toolbar;
    private String serverName;

    /**
     * Called when the activity is starting, inflates the activity's UI
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
        if (serverName == null)
            deleteServer.setVisibility(View.GONE);
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
            FlyveLog.e(ex.getMessage());
        }
    }

    private void instanceObject() {
        toolbar = findViewById(R.id.toolbar);
        deleteServer = toolbar.findViewById(R.id.deleteServer);
        editUrlAddress = findViewById(R.id.editUrlAddress);
        editTag = findViewById(R.id.editTag);
        editLogin = findViewById(R.id.editLogin);
        editPassWord = findViewById(R.id.editPassWord);
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
                    presenter.saveServer(serverInfo);
                } else {
                    presenter.updateServer(serverInfo, serverName);
                }
                break;
            case R.id.deleteServer:
                presenter.deleteServer(serverName);
                break;
        }
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityDetailServer.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void successful(String message) {
        Helpers.snackClose(ActivityDetailServer.this, message, getString(R.string.permission_snack_ok), true);
    }
}
