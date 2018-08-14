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
 * @author    @rafaelje
 * @date      23/6/18
 * @copyright Copyright © 2018 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.ListView;

import org.flyve.inventory.agent.core.home.Home;
import org.flyve.inventory.agent.core.home.HomeModel;
import org.flyve.inventory.agent.core.home.HomeSchema;
import org.flyve.inventory.agent.ui.ActivityMain;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class HomeModelTest {

    @Rule
    public ActivityTestRule<ActivityMain> rule  = new ActivityTestRule<>(ActivityMain.class);

    @Test
    public void setupList() {
        Home.Presenter presenter = mock(Home.Presenter.class);
        HomeModel homeModel = new HomeModel(presenter);
        ListView lst = mock(ListView.class);

        Activity activity = rule.getActivity();
        homeModel.setupList(activity, lst);
    }

    @Test
    public void clickItem() {
        Home.Presenter presenter = mock(Home.Presenter.class);
        HomeModel homeModel = new HomeModel(presenter);

        KeyEvent kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);

        Activity activity = rule.getActivity();
        HomeSchema homeSchema = new HomeSchema("1", "");
        homeModel.clickItem(activity, homeSchema);
        rule.getActivity().dispatchKeyEvent(kdown);
    }
}