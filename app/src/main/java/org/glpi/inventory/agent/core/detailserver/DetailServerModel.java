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
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.LocalPreferences;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailServerModel implements DetailServer.Model {

    private DetailServer.Presenter presenter;

    DetailServerModel(DetailServer.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void saveServer(ArrayList<String> modelServer, Context context) {
        LocalPreferences preferences = new LocalPreferences(context);
        if (modelServer.size() >= 5) {
            if (!"".equals(modelServer.get(0))) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("address", modelServer.get(0));
                    jo.put("tag", modelServer.get(1));
                    jo.put("login", modelServer.get(2));
                    jo.put("pass", modelServer.get(3));
                    jo.put("itemtype", modelServer.get(4));
                    jo.put("serial", modelServer.get(5));
                    ArrayList<String> serverArray = preferences.loadServer();
                    serverArray.add(modelServer.get(0));
                    preferences.saveServer(serverArray);
                } catch (JSONException e) {
                    AgentLog.e(e.getMessage());
                    presenter.showError("Error");
                }
                preferences.saveJSONObject(modelServer.get(0), jo);
                presenter.successful("Successful");
            } else {
                presenter.showError("Missing server");
            }
        } else {
            presenter.showError("Error");
        }
    }

    @Override
    public void updateServer(ArrayList<String> modelServer, String serverName, Context context) {
        LocalPreferences preferences = new LocalPreferences(context);
        if (modelServer.size() >= 5) {
            if (!"".equals(modelServer.get(0))) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("address", modelServer.get(0));
                    jo.put("tag", modelServer.get(1));
                    jo.put("login", modelServer.get(2));
                    jo.put("pass", modelServer.get(3));
                    jo.put("itemtype", modelServer.get(4));
                    jo.put("serial", modelServer.get(5));
                    ArrayList<String> serverArray = preferences.loadServer();
                    for (int i = 0; i < serverArray.size(); i++) {
                        if (serverArray.get(i).equals(serverName)) {
                            serverArray.set(i, modelServer.get(0));
                            preferences.saveServer(serverArray);
                        }
                    }
                } catch (JSONException e) {
                    AgentLog.e(e.getMessage());
                    presenter.showError("Error");
                }
                preferences.deletePreferences(serverName);
                preferences.saveJSONObject(modelServer.get(0), jo);
                presenter.successful("Successful");
            } else {
                presenter.showError("Missing Server");
            }
        } else {
            presenter.showError("Error");
        }
    }

    @Override
    public void loadServer(String serverName, Context context) {
        LocalPreferences preferences = new LocalPreferences(context);
        try {
            JSONObject jo = preferences.loadJSONObject(serverName);
            ServerSchema serverSchema = new ServerSchema();
            serverSchema.setAddress(jo.getString("address"));
            serverSchema.setTag(jo.getString("tag"));
            serverSchema.setLogin(jo.getString("login"));
            serverSchema.setPass(jo.getString("pass"));
            serverSchema.setItemtype(jo.getString("itemtype"));
            serverSchema.setSerial(jo.getString("serial"));
            presenter.modelServer(serverSchema);
        } catch (JSONException e) {
            AgentLog.e(e.getMessage());
            presenter.showError("Error");
        }
    }

    @Override
    public void deleteServer(String serverName, Context context) {
        if (serverName != null && !"".equals(serverName)) {
            LocalPreferences preferences = new LocalPreferences(context);
            preferences.deletePreferences(serverName);
            ArrayList<String> serverArray = preferences.loadServer();
            serverArray.remove(serverName);
            preferences.saveServer(serverArray);
            presenter.successful("Successful Delete");
        } else {
            presenter.showError("Error");
        }
    }
}
