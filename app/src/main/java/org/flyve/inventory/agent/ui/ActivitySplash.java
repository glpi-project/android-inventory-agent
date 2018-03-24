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
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.utils.LocalStorage;

public class ActivitySplash extends Activity {

    private static final int SPLASH_TIME = 3000;

    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        LocalStorage localStorage = new LocalStorage(ActivitySplash.this);

        String crashReport = localStorage.getData("crashReport");
        if(crashReport==null) {
            localStorage.setData("crashReport", "true");
        }

        String anonymousData = localStorage.getData("anonymousData");
        if(anonymousData==null) {
            localStorage.setData("anonymousData", "true");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                openActivity();

            }
        }, SPLASH_TIME);
    }

    /**
     * Starts the activity
     */
    private void openActivity() {
        Intent miIntent = new Intent(ActivitySplash.this, ActivityMain.class);
        ActivitySplash.this.startActivity(miIntent);
        ActivitySplash.this.finish();
    }
}
