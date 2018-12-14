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
 * @author    Ivan Del Pino - <idelpino@teclib.com>
 * @copyright Copyright © 2018 Teclib'
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.core.categories;

import android.content.Context;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.utils.FlyveLog;

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
            FlyveLog.e(e.getMessage());
            presenter.showError("Error");
        }
    }
}
