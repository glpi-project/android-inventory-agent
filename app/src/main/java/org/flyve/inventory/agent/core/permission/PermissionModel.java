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
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.core.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.flyve.inventory.agent.ui.ActivityMain;
import org.flyve.inventory.agent.utils.Helpers;

public class PermissionModel implements Permission.Model {

    private Permission.Presenter presenter;

    public PermissionModel(Permission.Presenter presenter) {
        this.presenter = presenter;
    }

    public void requestPermission(final Activity activity) {
        boolean isGranted = true;
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if(result != PackageManager.PERMISSION_GRANTED) {
            isGranted = false;
        }

        result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result != PackageManager.PERMISSION_GRANTED) {
            isGranted = false;
        }

        if(!isGranted) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA,
                    },
                    1);
        }
    }

    @Override
    public void openMain(Activity activity) {
        Helpers.openActivity(activity, ActivityMain.class, true);
    }
}
