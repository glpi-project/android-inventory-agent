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

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.core.report.Report;
import org.flyve.inventory.agent.core.report.ReportPresenter;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;

public class ActivityInventoryReport extends AppCompatActivity implements Report.View {

    private Report.Presenter presenter;

    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory_report);

        presenter = new ReportPresenter(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        try {
            setSupportActionBar(toolbar);
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

        FloatingActionButton btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showDialogShare(ActivityInventoryReport.this);
            }
        });

        RecyclerView lst = findViewById(R.id.lst);

        GridLayoutManager llm = new GridLayoutManager(ActivityInventoryReport.this, 1);
        lst.setLayoutManager(llm);

        presenter.generateReport(ActivityInventoryReport.this, lst);
    }


    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityInventoryReport.this, message, getString(R.string.permission_snack_ok), true);
    }
}
