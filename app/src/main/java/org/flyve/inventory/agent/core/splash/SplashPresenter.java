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

import android.content.Context;

public class SplashPresenter implements Splash.Presenter {

    private Splash.View view;
    private Splash.Model model;

    public SplashPresenter(Splash.View view){
        this.view = view;
        model = new SplashModel(this);
    }


    @Override
    public void showError(String message) {
        if(view!=null) {
            view.showError(message);
        }
    }

    @Override
    public void setupStorageReady() {
        if(view!=null) {
            view.setupStorageReady();
        }
    }

    @Override
    public void setupStorage(Context context) {
        model.setupStorage(context);
    }

}
