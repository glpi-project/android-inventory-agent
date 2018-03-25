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
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.core.home;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.HomeAdapter;
import org.flyve.inventory.agent.preference.GlobalParametersPreference;
import org.flyve.inventory.agent.preference.InventoryParametersPreference;
import org.flyve.inventory.agent.service.InventoryService;
import org.flyve.inventory.agent.ui.ActivityInventoryReport;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;

import java.util.ArrayList;
import java.util.List;

public class HomeModel implements Home.Model {

    private Home.Presenter presenter;
    private ArrayList<HomeSchema> arrHome;
    private Intent mServiceIntent;

    public HomeModel(Home.Presenter presenter) {
        this.presenter = presenter;
    }

    public void doBindService(Activity activity) {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        InventoryService inventoryService = new InventoryService();
        mServiceIntent = new Intent(activity, inventoryService.getClass());

        if (!isMyServiceRunning(activity, inventoryService.getClass())) {
            ComponentName result = activity.startService(mServiceIntent);

            if (result != null) {
                FlyveLog.log(this, " Agent started ", Log.INFO);
            } else {
                FlyveLog.log(this, " Agent fail", Log.ERROR);
            }
        } else {
            FlyveLog.log(this, " Agent already started ", Log.ERROR);
        }
    }

    /**
     * Check if the service is running
     * @param serviceClass Class
     * @return boolean
     */
    private boolean isMyServiceRunning(Activity activity, Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

        return false;
    }


    @Override
    public void setupList(Activity activity, ListView lst) {
        arrHome = new ArrayList<>();

        arrHome.add(new HomeSchema("1", activity.getString(R.string.AccueilInventoryTitle)));
        arrHome.add(new HomeSchema("4", activity.getString(R.string.AccueilInventoryShow), activity.getString(R.string.AccueilInventoryShowSummary)));
        arrHome.add(new HomeSchema("3", activity.getString(R.string.AccueilInventoryRun), activity.getString(R.string.AccueilInventoryRunSummary)));
        arrHome.add(new HomeSchema("5", activity.getString(R.string.AccueilInventoryParam), activity.getString(R.string.AccueilInventoryParamSummary)));
        arrHome.add(new HomeSchema("6", activity.getString(R.string.AccueilGlobalTitle)));
        arrHome.add(new HomeSchema("7", activity.getString(R.string.AccueilGlobalParam), activity.getString(R.string.AccueilGlobalParamSummary)));

        HomeAdapter mAdapter = new HomeAdapter(activity, arrHome);
        lst.setAdapter(mAdapter);
    }

    @Override
    public void clickItem(final Activity activity, HomeSchema homeSchema) {
        if(homeSchema.getId().equals("7")) {
            Intent miIntent = new Intent(activity, GlobalParametersPreference.class);
            activity.startActivity(miIntent);
        }

        if(homeSchema.getId().equals("5")) {
            Intent miIntent = new Intent(activity, InventoryParametersPreference.class);
            activity.startActivity(miIntent);
        }

        // Show my inventory
        if(homeSchema.getId().equals("4")) {
            Intent miIntent = new Intent(activity, ActivityInventoryReport.class);
            activity.startActivity(miIntent);
        }

        // Sent inventory
        if(homeSchema.getId().equals("3")) {
            final ProgressDialog progressBar = ProgressDialog.show(activity, "Sending inventory", activity.getResources().getString(R.string.loading));

            final InventoryTask inventoryTask = new InventoryTask(activity, Helpers.getAgentDescription(activity));

            // Sending anonymous information
            inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
                @Override
                public void onTaskSuccess(String data) {
                    FlyveLog.d(data);
                    HttpInventory httpInventory = new HttpInventory(activity);
                    httpInventory.sendInventory(data, new HttpInventory.OnTaskCompleted() {
                        @Override
                        public void onTaskSuccess(String data) {
                            progressBar.dismiss();
                            Helpers.snackClose(activity, data, activity.getResources().getString(R.string.snackButton), false);
                            Helpers.sendAnonymousData(activity, inventoryTask);
                        }

                        @Override
                        public void onTaskError(String error) {
                            progressBar.dismiss();
                            presenter.showError(error);
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

    @Override
    public List<HomeSchema> getListItems() {
        return arrHome;
    }
}
