package org.flyve.inventory.agent;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentManager;
import android.widget.ListView;

import org.flyve.inventory.agent.core.main.Main;
import org.flyve.inventory.agent.core.main.MainModel;
import org.flyve.inventory.agent.ui.ActivityMain;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

/*
 *   Copyright © 2018 Teclib. All rights reserved.
 *
 *   This file is part of flyve-mdm-android
 *
 * flyve-mdm-android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
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
 * @author    rafaelhernandez
 * @date      23/6/18
 * @copyright Copyright © 2018 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */public class MainModelTest {

    @Rule
    public ActivityTestRule<ActivityMain> rule  = new ActivityTestRule<>(ActivityMain.class);


    @Test
    public void requestPermission() {
        Activity activity = rule.getActivity();
        Main.Presenter presenter = mock(Main.Presenter.class);
        MainModel mainModel = new MainModel(presenter);
        mainModel.requestPermission(activity);
    }

    @Test
    public void setupInventoryAlarm() {
        Activity activity = rule.getActivity();
        Main.Presenter presenter = mock(Main.Presenter.class);
        MainModel mainModel = new MainModel(presenter);
        mainModel.setupInventoryAlarm(activity);
    }

    @Test
    public void loadFragment() {
        Activity activity = rule.getActivity();
        Main.Presenter presenter = mock(Main.Presenter.class);
        MainModel mainModel = new MainModel(presenter);
        FragmentManager fragmentManager = rule.getActivity().getSupportFragmentManager();
        android.support.v7.widget.Toolbar toolbar = mock(android.support.v7.widget.Toolbar.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("name", activity.getResources().getString(R.string.drawer_inventory));
        map.put("img", "ic_info");

        mainModel.loadFragment(fragmentManager, toolbar, map);
    }

    @Test
    public void getMenuItem() {
        Activity activity = rule.getActivity();
        Main.Presenter presenter = mock(Main.Presenter.class);
        MainModel mainModel = new MainModel(presenter);
        mainModel.getMenuItem();
    }

    @Test
    public void setupDrawer() {
        Activity activity = rule.getActivity();
        Main.Presenter presenter = mock(Main.Presenter.class);
        MainModel mainModel = new MainModel(presenter);
        ListView lst = mock(ListView.class);
        mainModel.setupDrawer(activity, lst);
    }
}