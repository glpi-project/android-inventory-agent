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

package org.glpi.inventory.agent.core.about;

import android.content.Context;

public class AboutPresenter implements About.Presenter {

    private About.View view;
    private About.Model model;

    public AboutPresenter(About.View view){
        this.view = view;
        model = new AboutModel(this);
    }

    @Override
    public void showAboutSuccess(String message) {
        if(view!=null) {
            view.showAboutSuccess(message);
        }
    }

    @Override
    public void showAboutFail() {
        if(view!=null) {
            view.showAboutFail();
        }
    }

    @Override
    public void crashTestEasterEgg(Context context) {
        model.crashTestEasterEgg(context);
    }

    @Override
    public void loadAbout(Context context) {
        model.loadAbout(context);
    }
}
