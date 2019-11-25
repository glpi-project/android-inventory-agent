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

package org.glpi.inventory.agent.core.categories;

import android.content.Context;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.utils.AgentLog;

import java.util.ArrayList;
import java.util.Collections;

public class CategoriesModel implements Categories.Model {

    private Categories.Presenter presenter;

    CategoriesModel(Categories.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadCategory(final Context context) {
        try {
            String[] inventory = context.getResources().getStringArray(R.array.Inventory);
            ArrayList<String> categories = new ArrayList<>();
            Collections.addAll(categories, inventory);
            presenter.showCategory(categories);
        } catch (Exception e) {
            AgentLog.e(e.getMessage());
            presenter.showError("Error");
        }
    }
}
