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
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.core.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.DrawerAdapter;
import org.flyve.inventory.agent.ui.FragmentAbout;
import org.flyve.inventory.agent.ui.FragmentHelp;
import org.flyve.inventory.agent.ui.FragmentHome;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.LocalStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainModel implements Main.Model {

    private Main.Presenter presenter;
    private ArrayList<HashMap<String, String>> arrDrawer;

    public MainModel(Main.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupInventoryAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timeInventory = sharedPreferences.getString("timeInventory", "week");

        // week by default
        calendar.add(Calendar.DATE, 7);

        if(timeInventory.equalsIgnoreCase("day")) {
            calendar.add(Calendar.DATE, 1);
        }

        if(timeInventory.equalsIgnoreCase("month")) {
            calendar.add(Calendar.DATE, 30);
        }

        long dateTime = calendar.getTime().getTime();

        LocalStorage cache = new LocalStorage(context);
        if(cache.getDataLong("data")==0) {
            cache.setDataLong("data", dateTime);
        }
    }

    public void loadFragment(FragmentManager fragmentManager, android.support.v7.widget.Toolbar toolbar, Map<String, String> menuItem) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        toolbar.setTitle(menuItem.get("name"));

        // Home
        if (menuItem.get("id").equals("1")) {
            FragmentHome f = new FragmentHome();
            fragmentTransaction.replace(R.id.containerView, f).commit();
            return;
        }

        // Help
        if (menuItem.get("id").equals("4")) {
            FragmentHelp f = new FragmentHelp();
            fragmentTransaction.replace(R.id.containerView, f).commit();
            return;
        }

        // About
        if (menuItem.get("id").equals("5")) {
            FragmentAbout f = new FragmentAbout();
            fragmentTransaction.replace(R.id.containerView, f).commit();
            return;
        }
    }

    public List<HashMap<String, String>> getMenuItem() {
        return arrDrawer;
    }

    public Map<String, String> setupDrawer(Activity activity, ListView lst) {
        arrDrawer = new ArrayList<>();

        // Information
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("name", activity.getResources().getString(R.string.drawer_inventory));
        map.put("img", "ic_info");
        arrDrawer.add(map);

        // Help
        map = new HashMap<>();
        map.put("id", "4");
        map.put("name", activity.getResources().getString(R.string.drawer_help));
        map.put("img", "ic_help");
        arrDrawer.add(map);

        // About
        map = new HashMap<>();
        map.put("id", "5");
        map.put("name", activity.getResources().getString(R.string.drawer_about));
        map.put("img", "ic_about");
        arrDrawer.add(map);

        try {
            // load adapter
            DrawerAdapter adapter = new DrawerAdapter(activity, arrDrawer);
            lst.setAdapter(adapter);

            // Select Information on load //
            return arrDrawer.get(0);
        } catch(Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

        return null;
    }
}
