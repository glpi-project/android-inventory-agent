package org.flyve.inventory.agent;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.flyve.inventory.agent.core.splash.Splash;
import org.flyve.inventory.agent.core.splash.SplashModel;
import org.flyve.inventory.agent.ui.ActivityMain;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
 * @author    @rafaelje
 * @date      23/6/18
 * @copyright Copyright © 2018 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

@RunWith(AndroidJUnit4.class)
public class SplashModelTest {

    @Rule
    public ActivityTestRule<ActivityMain> rule  = new ActivityTestRule<>(ActivityMain.class);


    @Test
    public void setupStorage() {
        Activity activity = rule.getActivity();
        Splash.Presenter presenter = mock(Splash.Presenter.class);
        SplashModel splashModel = new SplashModel(presenter);
        splashModel.setupStorage(activity);
    }

    @Test
    public void nextActivityWithDelay() {
        Activity activity = rule.getActivity();
        Splash.Presenter presenter = mock(Splash.Presenter.class);
        SplashModel splashModel = new SplashModel(presenter);
        splashModel.nextActivityWithDelay(1, activity, ActivityMain.class);
    }
}