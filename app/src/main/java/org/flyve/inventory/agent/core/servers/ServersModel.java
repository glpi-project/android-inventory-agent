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
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.core.servers;

import android.app.Activity;

import org.flyve.inventory.agent.utils.LocalPreferences;

public class ServersModel implements Servers.Model {

    private Servers.Presenter presenter;

    ServersModel(Servers.Presenter presenter) {
        this.presenter = presenter;
    }

    public void loadServers(final Activity activity) {
        LocalPreferences preferences = new LocalPreferences(activity);
        presenter.sendServers(preferences.loadServerArray());
    }

}
