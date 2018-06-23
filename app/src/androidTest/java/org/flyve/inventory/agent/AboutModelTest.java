package org.flyve.inventory.agent;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.flyve.inventory.agent.core.about.About;
import org.flyve.inventory.agent.core.about.AboutModel;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
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
 */

@RunWith(AndroidJUnit4.class)
public class AboutModelTest {

    private Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void crashTestEasterEgg() {
        About.Presenter presenter = mock(About.Presenter.class);
        AboutModel model = new AboutModel(presenter);
        model.crashTestEasterEgg(context);
        assertTrue(true);
    }

    @Test
    public void loadAbout() {
        About.Presenter presenter = mock(About.Presenter.class);
        AboutModel model = new AboutModel(presenter);
        model.loadAbout(context);
        assertTrue(true);
    }

}