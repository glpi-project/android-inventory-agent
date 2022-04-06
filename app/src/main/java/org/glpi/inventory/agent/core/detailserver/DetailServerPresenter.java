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

package org.glpi.inventory.agent.core.detailserver;

import android.content.Context;

import org.glpi.inventory.agent.schema.ServerSchema;

import java.util.ArrayList;

public class DetailServerPresenter implements DetailServer.Presenter {

    private DetailServer.View view;
    private DetailServer.Model model;

    public  DetailServerPresenter(DetailServer.View view){
        this.view = view;
        model = new DetailServerModel(this);
    }

    @Override
    public void showError(String message) {
        if (view != null) {
            view.showError(message);
        }
    }

    @Override
    public void successful(String message) {
        if (view != null) {
            view.successful(message);
        }
    }

    @Override
    public void modelServer(ServerSchema model) {
        if (view != null) {
            view.modelServer(model);
        }
    }

    @Override
    public void saveServer(ArrayList<String> message, Context applicationContext) {
        model.saveServer(message, applicationContext);
    }

    @Override
    public void deleteServer(String serverName, Context applicationContext) {
        model.deleteServer(serverName, applicationContext);
    }

    @Override
    public void updateServer(ArrayList<String> serverInfo, String serverName, Context applicationContext) {
        model.updateServer(serverInfo, serverName, applicationContext);
    }

    @Override
    public void loadServer(String serverName, Context applicationContext) {
        model.loadServer(serverName, applicationContext);
    }

}
