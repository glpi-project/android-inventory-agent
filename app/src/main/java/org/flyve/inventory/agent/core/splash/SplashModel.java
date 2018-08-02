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

package org.flyve.inventory.agent.core.splash;

import android.app.Activity;
import android.content.Context;

import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.LocalStorage;

public class SplashModel implements Splash.Model {

    private Splash.Presenter presenter;

    public SplashModel(Splash.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setupStorage(Context context) {
        try {
            LocalStorage localStorage = new LocalStorage(context);

            String crashReport = localStorage.getData("crashReport");
            if (crashReport == null) {
                localStorage.setData("crashReport", "true");
            }

            String anonymousData = localStorage.getData("anonymousData");
            if (anonymousData == null) {
                localStorage.setData("anonymousData", "true");
            }

            presenter.setupStorageReady();
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
            presenter.showError(ex.getMessage());
        }
    }
}
