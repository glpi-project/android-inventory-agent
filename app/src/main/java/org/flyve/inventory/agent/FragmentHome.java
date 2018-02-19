package org.flyve.inventory.agent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.adapter.HomeAdapter;
import org.flyve.inventory.agent.core.home.HomeSchema;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.HttpInventory;

import java.util.ArrayList;

/*
 *   Copyright © 2018 Teclib. All rights reserved.
 *
 *   This file is part of flyve-mdm-android
 *
 * flyve-mdm-android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
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
 * @date      18/2/18
 * @copyright Copyright © 2018 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */
public class FragmentHome extends Fragment {

    private ArrayList<HomeSchema> arrHome;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null);

        arrHome = new ArrayList<>();

        arrHome.add(new HomeSchema("1", getString(R.string.AccueilInventoryTitle)));
        arrHome.add(new HomeSchema("2", getString(R.string.AccueilInventoryTitle), getString(R.string.AccueilInventorySummaryOn), false));
        arrHome.add(new HomeSchema("3", getString(R.string.AccueilInventoryRun), getString(R.string.AccueilInventoryRunSummary)));
        arrHome.add(new HomeSchema("4", getString(R.string.AccueilInventoryShow), getString(R.string.AccueilInventoryShowSummary)));
        arrHome.add(new HomeSchema("5", getString(R.string.AccueilInventoryParam), getString(R.string.AccueilInventoryParamSummary)));
        arrHome.add(new HomeSchema("6", getString(R.string.AccueilGlobalTitle)));
        arrHome.add(new HomeSchema("7", getString(R.string.AccueilGlobalParam), getString(R.string.AccueilGlobalParamSummary)));

        ListView lst = v.findViewById(R.id.lst);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeSchema homeSchema = arrHome.get(i);

                // Show my inventory
                if(homeSchema.getId().equals("4")) {
                    Intent miIntent = new Intent(FragmentHome.this.getContext(), InventoryActivity.class);
                    FragmentHome.this.startActivity(miIntent);
                }

                // Sent inventory
                if(homeSchema.getId().equals("3")) {
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
                                    Helpers.snackClose(FragmentHome.this.getActivity(), data, FragmentHome.this.getResources().getString(R.string.snackButton), false);
                                    Helpers.sendAnonymousData(FragmentHome.this.getContext(), inventoryTask);
                                }

                                @Override
                                public void onTaskError(String error) {
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
