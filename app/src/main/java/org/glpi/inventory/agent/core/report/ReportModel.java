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

package org.glpi.inventory.agent.core.report;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.LocalPreferences;
import org.glpi.inventory.agent.utils.Utils;

import java.util.ArrayList;

public class ReportModel implements Report.Model {

    private Report.Presenter presenter;

    ReportModel(Report.Presenter presenter) {
        this.presenter = presenter;
    }

    public void generateReport(final Activity activity) {
        LocalPreferences preferences = new LocalPreferences(activity);
        final ArrayList<String> listPreference = preferences.loadCategories();
        String description = Helpers.getAgentDescription(activity);
        final InventoryTask inventoryTask;

        if (listPreference.size() > 0) {
            String[] categories = listPreference.toArray(new String[listPreference.size()]);
            inventoryTask = new InventoryTask(activity, description, true, categories);
        } else {
            inventoryTask = new InventoryTask(activity, description, true);
        }

        inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String s) {
                inventoryTask.getXMLSyn();
                presenter.sendInventory(s, Utils.loadJsonHeader(s));
            }

            @Override
            public void onTaskError(Throwable throwable) {
                presenter.showError(throwable.getMessage());
            }
        });
    }

    public void showDialogShare(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_share_title);

        final int[] type = new int[1];

        //list of items
        String[] items = context.getResources().getStringArray(R.array.export_list);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        type[0] = which;
                    }
                });

        String positiveText = context.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        Helpers.share( context, "Inventory Agent File", type[0] );
                    }
                });

        String negativeText = context.getString(android.R.string.cancel);
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
