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

package org.glpi.inventory.agent.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Properties;

public class EnvironmentInfo {
    private Properties properties = new Properties();

    private Boolean isLoaded = false;

    public EnvironmentInfo(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("about.properties");
            properties.load(inputStream);
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
            isLoaded = false;
        }
        isLoaded = true;
    }

    public Boolean getIsLoaded() {
        return isLoaded;
    }

    public String getVersion() {
        return properties.getProperty("about.version");
    }

    public String getBuild() {
        return properties.getProperty("about.build");
    }

    public String getDate() {
        return properties.getProperty("about.date");
    }

    public String getCommit() {
        return properties.getProperty("about.commit");
    }

    public String getCommitFull() {
        return properties.getProperty("about.commitFull");
    }

    public String getGithub() {
        return properties.getProperty("about.github");
    }

}
