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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.home.Home;
import org.glpi.inventory.agent.core.home.HomePresenter;
import org.glpi.inventory.agent.utils.Helpers;

public class FragmentHome extends Fragment implements Home.View {

    private Home.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null);

        presenter = new HomePresenter(this);

        presenter.doBindService(FragmentHome.this.getActivity());

        Button btn_run = v.findViewById(R.id.btn_run_inventory);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogListServers alert = new DialogListServers();
                alert.showDialog(FragmentHome.this.getActivity(), presenter);
            }
        });

        Button btn_show = v.findViewById(R.id.btn_show_inventory);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(FragmentHome.this.getActivity(), ActivityInventoryReport.class);
                FragmentHome.this.getActivity().startActivity(miIntent);
            }
        });


        //ListView lst = v.findViewById(R.id.lst);
        /*presenter.setupList(FragmentHome.this.getActivity(), lst);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeSchema homeSchema = presenter.getListItems().get(i);
                presenter.clickItem(FragmentHome.this.getActivity(), homeSchema);
            }
        });*/

        return v;
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(FragmentHome.this.getActivity(), message, getResources().getString(R.string.snackButton), true);
    }
}
