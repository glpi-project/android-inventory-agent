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

package org.glpi.inventory.agent.core.home;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.adapter.HomeAdapter;
import org.glpi.inventory.agent.preference.GlobalParametersPreference;
import org.glpi.inventory.agent.preference.InventoryParametersPreference;
import org.glpi.inventory.agent.ui.ActivityInventoryReport;
import org.glpi.inventory.agent.ui.DialogListServers;

import java.util.ArrayList;
import java.util.List;

public class HomeModel implements Home.Model {

    private Home.Presenter presenter;
    private ArrayList<HomeSchema> arrHome;

    public HomeModel(Home.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupList(Activity activity, ListView lst) {
        arrHome = new ArrayList<>();

        arrHome.add(new HomeSchema("1", activity.getString(R.string.AccueilInventoryTitle)));
        arrHome.add(new HomeSchema("4", activity.getString(R.string.AccueilInventoryShow), activity.getString(R.string.AccueilInventoryShowSummary)));
        arrHome.add(new HomeSchema("3", activity.getString(R.string.AccueilInventoryRun), activity.getString(R.string.AccueilInventoryRunSummary)));
        //arrHome.add(new HomeSchema("5", activity.getString(R.string.AccueilInventoryParam), activity.getString(R.string.AccueilInventoryParamSummary)));
        //arrHome.add(new HomeSchema("6", activity.getString(R.string.AccueilGlobalTitle)));
        //arrHome.add(new HomeSchema("7", activity.getString(R.string.AccueilGlobalParam), activity.getString(R.string.AccueilGlobalParamSummary)));

        HomeAdapter mAdapter = new HomeAdapter(activity, arrHome);
        lst.setAdapter(mAdapter);
    }

    @Override
    public void clickItem(final Activity activity, HomeSchema homeSchema) {
        if (homeSchema.getId().equals("7")) {
            Intent miIntent = new Intent(activity, GlobalParametersPreference.class);
            activity.startActivity(miIntent);
        }

        if (homeSchema.getId().equals("5")) {
            Intent miIntent = new Intent(activity, InventoryParametersPreference.class);
            activity.startActivity(miIntent);
        }

        // Show my inventory
        if (homeSchema.getId().equals("4")) {
            Intent miIntent = new Intent(activity, ActivityInventoryReport.class);
            activity.startActivity(miIntent);
        }

        // Sent inventory
        if (homeSchema.getId().equals("3")) {
            DialogListServers alert = new DialogListServers();
            alert.showDialog(activity, presenter);
        }
    }

    @Override
    public List<HomeSchema> getListItems() {
        return arrHome;
    }
}
