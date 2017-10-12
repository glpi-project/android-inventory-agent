/*
 * Copyright (C) 2017 Teclib'
 *
 * This file is part of Flyve MDM Inventory Agent Android.
 *
 * Flyve MDM Inventory Agent Android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
 *
 * Flyve MDM Android is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM Inventory Agent Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez - rafaelje
 * @copyright Copyright (c) 2017 Flyve MDM
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent/
 * @link      http://www.glpi-project.org/
 * @link      https://flyve-mdm.com/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import org.flyve.inventory.agent.utils.LocalStorage;

public class SplashActivity extends Activity {

    private static final int SPLASH_TIME = 3000;

    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param Bundle savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        LocalStorage localStorage = new LocalStorage(SplashActivity.this);

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
        Intent miIntent = new Intent(SplashActivity.this, FragmentAccueil.class);
        SplashActivity.this.startActivity(miIntent);
        SplashActivity.this.finish();
    }
}
