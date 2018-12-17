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

package org.flyve.inventory.agent.core.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Main {

    interface View {
        void showError(String message);
    }

    interface Presenter {
        // Views
        void showError(String message);

        // Models
        Map<String, String> setupDrawer(Activity activity, ListView lst);
        void loadFragment(FragmentManager fragmentManager, android.support.v7.widget.Toolbar toolbar, Map<String, String> item);
        List<HashMap<String, String>> getMenuItem();
        void setupInventoryAlarm(Context context);
    }

    interface Model {
        Map<String, String> setupDrawer(Activity activity, ListView lst);
        void loadFragment(FragmentManager fragmentManager, android.support.v7.widget.Toolbar toolbar, Map<String, String> item);
        List<HashMap<String, String>> getMenuItem();
        void setupInventoryAlarm(Context context);
    }
}
