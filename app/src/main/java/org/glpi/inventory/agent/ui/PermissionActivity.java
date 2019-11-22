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

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
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
