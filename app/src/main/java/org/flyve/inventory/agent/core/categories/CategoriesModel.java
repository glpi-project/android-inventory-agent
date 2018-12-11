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

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.Utils;

import java.util.ArrayList;

public class CategoriesModel implements Categories.Model {

    private Categories.Presenter presenter;

    CategoriesModel(Categories.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadCategory(Context context) {
        try {
            final InventoryTask inventoryTask = new InventoryTask(context, Helpers.getAgentDescription(context), true);
            inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
                @Override
                public void onTaskSuccess(String s) {
                    ArrayList<String> model = Utils.loadJsonHeader(s);
                    presenter.showCategory(model);
                }

                @Override
                public void onTaskError(Throwable throwable) {
                    presenter.showError(throwable.getMessage());
                }
            });
        } catch (Exception e) {
            FlyveLog.e(e.getMessage());
            presenter.showError("Error");
        }
    }
}
