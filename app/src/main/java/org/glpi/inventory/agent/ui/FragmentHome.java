/**
 *  LICENSE
 *
 *  This file is part of Flyve MDM Inventory Agent for Android.
 * 
 *  Inventory Agent for Android is a subproject of Flyve MDM.
 *  Flyve MDM is a mobile device management software.
 *
 *  Flyve MDM is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  Flyve MDM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @copyright Copyright © 2018 Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/flyve-mdm/android-inventory-agent
 *  @link      https://flyve-mdm.com
 *  @link      http://flyve.org/android-inventory-agent
 *  ---------------------------------------------------------------------
 */

package org.glpi.inventory.agent.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.home.Home;
import org.glpi.inventory.agent.core.home.HomePresenter;
import org.glpi.inventory.agent.core.home.HomeSchema;
import org.glpi.inventory.agent.utils.Helpers;

public class FragmentHome extends Fragment implements Home.View {

    private Home.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, null);

        presenter = new HomePresenter(this);

        presenter.doBindService(FragmentHome.this.getActivity());

        ListView lst = v.findViewById(R.id.lst);
        presenter.setupList(FragmentHome.this.getActivity(), lst);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeSchema homeSchema = presenter.getListItems().get(i);
                presenter.clickItem(FragmentHome.this.getActivity(), homeSchema);
            }
        });

        return v;
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(FragmentHome.this.getActivity(), message, getResources().getString(R.string.snackButton), true);
    }
}
