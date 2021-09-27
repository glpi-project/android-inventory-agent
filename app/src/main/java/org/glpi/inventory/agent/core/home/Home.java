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

package org.glpi.inventory.agent.core.home;

import android.app.Activity;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import java.util.List;

public interface Home {

    interface View {
        void showError(String message);
        void setToolbar(Toolbar toolbar);
    }

    interface Presenter {
        // Views
        void showError(String message);

        // Models
        void doBindService(Activity activity);
        void setupList(Activity activity, ListView lst);
        void clickItem(final Activity activity, HomeSchema homeSchema);
        List<HomeSchema> getListItems();
    }

    interface Model {
        void doBindService(Activity activity);
        void setupList(Activity activity, ListView lst);
        void clickItem(final Activity activity, HomeSchema homeSchema);
        List<HomeSchema> getListItems();
    }
}
