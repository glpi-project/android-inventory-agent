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
 * @author    Ivan Del Pino - <idelpino@teclib.com>
 * @copyright Copyright © 2018 Teclib'
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.CategoriesAdapter;
import org.flyve.inventory.agent.core.categories.Categories;
import org.flyve.inventory.agent.core.categories.CategoriesPresenter;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.Utils;

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
            FlyveLog.e(ex.getMessage());
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
