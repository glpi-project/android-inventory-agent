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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.core.home.Home;
import org.flyve.inventory.agent.model.ServerModel;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;
import org.flyve.inventory.agent.utils.LocalPreferences;

import java.util.ArrayList;

public class DialogListServers {

    private Dialog dialog;
    private Spinner spinnerServers;

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
        ArrayList<String> serverArray = new LocalPreferences(activity).loadServerArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner_item, serverArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServers = dialog.findViewById(R.id.spinnerServers);
        spinnerServers.setAdapter(adapter);
    }

    private void sendInventory(final Activity activity, final Home.Presenter presenter) {
        String message = activity.getResources().getString(R.string.loading);
        final ProgressDialog progressBar = ProgressDialog.show(activity, "Sending inventory", message);

        final InventoryTask inventoryTask = new InventoryTask(activity, Helpers.getAgentDescription(activity));

        // Sending anonymous information
        inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                FlyveLog.d(data);
                HttpInventory httpInventory = new HttpInventory(activity);
                String serverName = spinnerServers.getSelectedItem().toString();
                ServerModel model = httpInventory.setServerModel(serverName);
                httpInventory.sendInventory(data, model, new HttpInventory.OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(String data) {
                        progressBar.dismiss();
                        String action = activity.getResources().getString(R.string.snackButton);
                        Helpers.snackClose(activity, data, action, false);
                        Helpers.sendAnonymousData(activity, inventoryTask);
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
                FlyveLog.e(error.getMessage());
                presenter.showError(error.getMessage());
            }
        });
    }
}