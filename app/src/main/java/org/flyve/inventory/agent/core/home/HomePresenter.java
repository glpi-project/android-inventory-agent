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

package org.flyve.inventory.agent.core.home;

import android.app.Activity;
import android.widget.ListView;

import java.util.List;

public class HomePresenter implements Home.Presenter {

    private Home.View view;
    private Home.Model model;

    public HomePresenter(Home.View view){
        this.view = view;
        model = new HomeModel(this);
    }

    @Override
    public void showError(String message) {
        if(view!=null) {
            view.showError(message);
        }
    }

    @Override
    public void doBindService(Activity activity) {
        model.doBindService(activity);
    }

    @Override
    public void setupList(Activity activity, ListView lst) {
        model.setupList(activity, lst);
    }

    @Override
    public void clickItem(Activity activity, HomeSchema homeSchema) {
        model.clickItem(activity, homeSchema);
    }

    @Override
    public List<HomeSchema> getListItems() {
        return model.getListItems();
    }

}
