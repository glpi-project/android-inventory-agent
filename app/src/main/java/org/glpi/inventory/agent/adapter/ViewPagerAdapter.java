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

package org.glpi.inventory.agent.adapter;

import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.glpi.inventory.agent.ui.FragmentInventoryList;

import java.util.ArrayList;
import java.util.Collections;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String data;
    private ArrayList<String> listInventory;
    private ArrayList<String> load;

    public ViewPagerAdapter(FragmentManager fm, String data, ArrayList<String> listInventory, ArrayList<String> load, ProgressBar progressBar) {
        super(fm);
        this.data = data;
        this.listInventory = listInventory;
        this.load = load;
        this.load.remove("");
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentInventoryList.newInstance(data, listInventory.get(position));
    }

    @Override
    public int getCount() {
        return listInventory.size() - Collections.frequency(listInventory, "");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return load.get(position);
    }
}
