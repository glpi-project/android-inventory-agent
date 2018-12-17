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

package org.flyve.inventory.agent.core.about;

import android.content.Context;

public interface About {

    interface View {
        void showAboutSuccess(String message);
        void showAboutFail();
    }

    interface Presenter {
        // Views
        void showAboutSuccess(String message);
        void showAboutFail();

        // Models
        void crashTestEasterEgg(Context context);
        void loadAbout(Context context);
    }

    interface Model {
        void crashTestEasterEgg(Context context);
        void loadAbout(Context context);
    }
}
