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

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.permission.Permission;
import org.glpi.inventory.agent.core.permission.PermissionPresenter;
import org.glpi.inventory.agent.utils.Helpers;

public class PermissionActivity extends AppCompatActivity implements Permission.View {

    Permission.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        presenter = new PermissionPresenter(PermissionActivity.this);

        if(Build.VERSION.SDK_INT < 23) {
            permissionSuccess();
        }
    }

    public void requestPermission(View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            presenter.requestPermission(PermissionActivity.this);
        } else {
            permissionSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    presenter.permissionSuccess();
                } else {
                    presenter.showError(getString(R.string.permission_error_result));
                }
            }
        }
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(PermissionActivity.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void permissionSuccess() {
        presenter.openMain(PermissionActivity.this);
    }
}
