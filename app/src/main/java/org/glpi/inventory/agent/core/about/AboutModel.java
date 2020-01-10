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

package org.glpi.inventory.agent.core.about;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.Toast;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.utils.EnvironmentInfo;
import org.glpi.inventory.agent.utils.AgentLog;

public class AboutModel implements About.Model {

    private About.Presenter presenter;
    private int countEasterEgg;

    public AboutModel(About.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void crashTestEasterEgg(Context context) {
        countEasterEgg++;

        if (countEasterEgg > 6 && countEasterEgg <= 10) {
            Toast.makeText(context, context.getResources().getQuantityString(R.plurals.easter_egg_attempts, countEasterEgg, countEasterEgg), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void loadAbout(Context context) {
        EnvironmentInfo environmentInfo = new EnvironmentInfo(context);

        if(environmentInfo.getIsLoaded()) {
            String about = "";

            try {
                about = Html.fromHtml(
                        aboutStr(
                                environmentInfo.getVersion(),
                                environmentInfo.getBuild(),
                                environmentInfo.getDate(),
                                environmentInfo.getCommit(),
                                environmentInfo.getCommitFull(),
                                environmentInfo.getGithub()
                        )
                ).toString();
            } catch (Exception ex) {
                AgentLog.e(ex.getMessage());
            }

            presenter.showAboutSuccess(about);
        } else {
            presenter.showAboutFail();
        }
    }

    private String aboutStr(String version, String build, String date, String commit, String commitFull, String github) {
        String str = "Inventory Agent, version "+ version +", build "+ build +".<br />";
        str += "Built on "+ date + ".<br />" /*+". Last commit <a href='"+github+"/commit/"+commitFull+"'>"+ commit +"</a>.<br />"*/;
        str += "© <a href='http://teclib-edition.com/'>Teclib'</a>. Licensed under <a href='https://www.gnu.org/licenses/gpl-3.0.en.html'>GPLv3</a>. <a href='https://flyve-mdm.com/'>GLPI Android Inventory Agent</a>®<br />See us on GitHub<br />";

        return str;
    }
}
