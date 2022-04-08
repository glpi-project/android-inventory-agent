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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.home.Home;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.HttpInventory;
import org.glpi.inventory.agent.utils.LocalPreferences;
import org.glpi.inventory.agent.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class DialogListServers {

    private Dialog dialog;
    private Spinner spinnerServers;
    private final String TOALLSERVERS = "Send to all servers";

    public void showDialog(final Activity activity, final Home.Presenter presenter){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_list_servers);

        setSpinner(activity);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInventory(activity, presenter);
            }
        });

        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    private void setSpinner(Activity activity) {
        ArrayList<String> serverArray = new LocalPreferences(activity).loadServer();
        if (!serverArray.isEmpty()) {
            serverArray.add(0, TOALLSERVERS);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner_item, serverArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerServers = dialog.findViewById(R.id.spinnerServers);
            spinnerServers.setAdapter(adapter);
        } else {
            dialog.findViewById(R.id.containerNoServer).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.containerSpinner).setVisibility(View.GONE);
            dialog.findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void sendInventory(final Activity activity, final Home.Presenter presenter) {
        final String serverName = spinnerServers.getSelectedItem().toString();
        if (!TOALLSERVERS.equalsIgnoreCase(serverName)) {
            sendInventory(activity, presenter, serverName);
        } else {
            ArrayList<String> serverArray = new LocalPreferences(activity).loadServer();
            for (final String server : serverArray) {
                sendInventory(activity, presenter, server);
            }
        }
    }

    private void sendInventory(final Activity activity, final Home.Presenter presenter, String server) {
        String message = activity.getResources().getString(R.string.loading);
        final ProgressDialog progressBar = ProgressDialog.show(activity, "Sending inventory", message);

        final InventoryTask inventoryTask = new InventoryTask(activity, Helpers.getAgentDescription(activity), true);
        final HttpInventory httpInventory = new HttpInventory(activity);
        final ServerSchema model = httpInventory.setServerModel(server);
        inventoryTask.setTag(model.getTag());
        inventoryTask.setAssetItemtype(model.getItemtype());

        // Sending anonymous information
        inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                AgentLog.d(data);
                httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        progressBar.dismiss();
                        String action = activity.getResources().getString(R.string.snackButton);
                        Helpers.snackClose(activity, data, action, false);
                        //Helpers.sendAnonymousData(activity, inventoryTask);
                        dialog.dismiss();
                    }

                    @Override
                    public void onTaskError(String error) {
                        progressBar.dismiss();
                        presenter.showError(error);
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onTaskError(Throwable error) {
                AgentLog.e(error.getMessage());
                presenter.showError(error.getMessage());
            }
        });
    }
}