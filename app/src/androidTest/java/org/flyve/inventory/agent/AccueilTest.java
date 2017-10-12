package org.flyve.inventory.agent;

/*
 *   Copyright © 2017 Teclib. All rights reserved.
 *
 *   This file is part of flyve-mdm-android-inventory-agent
 *
 * flyve-mdm-android-inventory-agent is a subproject of Flyve MDM. Flyve MDM is a mobile
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
 * @author    Rafael Hernandez
 * @date      26/9/17
 * @copyright Copyright © 2017 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

@RunWith(AndroidJUnit4.class)
public class AccueilTest {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<FragmentAccueil> activityRule = new ActivityTestRule<>(FragmentAccueil.class);

    @Test
    public void testTakeScreenshot() {
        if (Build.VERSION.SDK_INT < 24) {
            Screengrab.screenshot("Accueil");
        }
    }
}
