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

import java.util.ArrayList;

public interface Categories {

    interface View {
        void showError(String message);
        void showCategories(ArrayList<String> model);
    }

    interface Presenter {
        // Views
        void showError(String message);
        void showCategory(ArrayList<String> model);

        // Models
        void loadCategory(Context applicationContext);
    }

    interface Model {
        void loadCategory(Context applicationContext);
    }
}
