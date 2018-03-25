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

package org.flyve.inventory.agent.ui;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.HomeAdapter;
import org.flyve.inventory.agent.core.home.HomeSchema;
import org.flyve.inventory.agent.preference.GlobalParametersPreference;
import org.flyve.inventory.agent.preference.InventoryParametersPreference;
import org.flyve.inventory.agent.service.InventoryService;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    private ArrayList<HomeSchema> arrHome;
    private Intent mServiceIntent;

    private void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        InventoryService inventoryService = new InventoryService();
        mServiceIntent = new Intent(FragmentHome.this.getContext(), inventoryService.getClass());

        if (!isMyServiceRunning(inventoryService.getClass())) {
            ComponentName result = FragmentHome.this.getActivity().startService(mServiceIntent);

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
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) FragmentHome.this.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null);

        doBindService();

        arrHome = new ArrayList<>();

        arrHome.add(new HomeSchema("1", getString(R.string.AccueilInventoryTitle)));
        arrHome.add(new HomeSchema("4", getString(R.string.AccueilInventoryShow), getString(R.string.AccueilInventoryShowSummary)));
        arrHome.add(new HomeSchema("3", getString(R.string.AccueilInventoryRun), getString(R.string.AccueilInventoryRunSummary)));
        arrHome.add(new HomeSchema("5", getString(R.string.AccueilInventoryParam), getString(R.string.AccueilInventoryParamSummary)));
        arrHome.add(new HomeSchema("6", getString(R.string.AccueilGlobalTitle)));
        arrHome.add(new HomeSchema("7", getString(R.string.AccueilGlobalParam), getString(R.string.AccueilGlobalParamSummary)));

        ListView lst = v.findViewById(R.id.lst);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeSchema homeSchema = arrHome.get(i);

                if(homeSchema.getId().equals("7")) {
                    Intent miIntent = new Intent(FragmentHome.this.getContext(), GlobalParametersPreference.class);
                    FragmentHome.this.startActivity(miIntent);
                }

                if(homeSchema.getId().equals("5")) {
                    Intent miIntent = new Intent(FragmentHome.this.getContext(), InventoryParametersPreference.class);
                    FragmentHome.this.startActivity(miIntent);
                }

                // Show my inventory
                if(homeSchema.getId().equals("4")) {
                    Intent miIntent = new Intent(FragmentHome.this.getContext(), ActivityInventoryReport.class);
                    FragmentHome.this.startActivity(miIntent);
                }

                // Sent inventory
                if(homeSchema.getId().equals("3")) {
                    final ProgressDialog progressBar = ProgressDialog.show(FragmentHome.this.getActivity(), "Sending inventory", getResources().getString(R.string.loading));

                    final InventoryTask inventoryTask = new InventoryTask(FragmentHome.this.getContext(), Helpers.getAgentDescription(FragmentHome.this.getContext()));

                    // Sending anonymous information
                    inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
                        @Override
                        public void onTaskSuccess(String data) {
                            FlyveLog.d(data);
                            HttpInventory httpInventory = new HttpInventory(FragmentHome.this.getContext());
                            httpInventory.sendInventory(data, new HttpInventory.OnTaskCompleted() {
                                @Override
                                public void onTaskSuccess(String data) {
                                    progressBar.dismiss();
                                    Helpers.snackClose(FragmentHome.this.getActivity(), data, FragmentHome.this.getResources().getString(R.string.snackButton), false);
                                    Helpers.sendAnonymousData(FragmentHome.this.getContext(), inventoryTask);
                                }

                                @Override
                                public void onTaskError(String error) {
                                    progressBar.dismiss();
                                    Helpers.snackClose(FragmentHome.this.getActivity(), error, FragmentHome.this.getResources().getString(R.string.snackButton), true);
                                }
                            });
                        }

                        @Override
                        public void onTaskError(Throwable error) {
                            FlyveLog.e(error.getMessage());
                            Helpers.snackClose(FragmentHome.this.getActivity(), error.getMessage(), FragmentHome.this.getResources().getString(R.string.snackButton), true);
                        }
                    });

                }
            }
        });

        HomeAdapter mAdapter = new HomeAdapter(FragmentHome.this.getActivity(), arrHome);
        lst.setAdapter(mAdapter);

        return v;
    }
}
