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

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.adapter.CategoriesAdapter;
import org.glpi.inventory.agent.core.categories.Categories;
import org.glpi.inventory.agent.core.categories.CategoriesPresenter;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityCategories extends AppCompatActivity implements Categories.View {

    private ProgressBar progressBar;

    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);
        Categories.Presenter presenter = new CategoriesPresenter(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        try {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
        }

        presenter.loadCategory(this);
    }


    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityCategories.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void showCategories(ArrayList<String> load) {
        progressBar.setVisibility(View.GONE);
        load.remove("");
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, Utils.getFormatTitle(load));

        ArrayList<String> listTitle = new ArrayList<>();
        for (String value : list) {
            String resource = Utils.getStringResourceByName(value, this);
            listTitle.add(!resource.equals("") ? resource : value);
        }
        RecyclerView listServer = findViewById(R.id.recyclerListCategories);
        listServer.setVisibility(View.VISIBLE);
        listServer.setLayoutManager(new LinearLayoutManager(this));
        listServer.setAdapter(new CategoriesAdapter(load, listTitle, this));
    }
}
