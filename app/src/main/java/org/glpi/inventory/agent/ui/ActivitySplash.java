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

package org.glpi.inventory.agent.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.splash.Splash;
import org.glpi.inventory.agent.core.splash.SplashPresenter;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;

public class ActivitySplash extends Activity implements Splash.View {

    private static final int DELAY = 4000;
    private Splash.Presenter presenter;
    private View view;
    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        presenter = new SplashPresenter(this);
        presenter.setupStorage(ActivitySplash.this);

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        view = li.inflate(R.layout.activity_splash, null);
        setContentView(R.layout.activity_splash);


        ImageView logoTeclib = (ImageView) findViewById(R.id.imgLogoTeclib);
        Animation fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        logoTeclib.setAnimation(fromBottom);

        ImageView logoGlpi = findViewById(R.id.imgInventory);
        Animation fromTop = AnimationUtils.loadAnimation(this,R.anim.from_top);
        logoGlpi.setAnimation(fromTop);

    }


    void fadeOutAnimation() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOut.setDuration(1500);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // We wanna set the view to GONE, after it's fade out. so it actually disappear from the layout & don't take up space.
                Helpers.openActivity(ActivitySplash.this, ActivityMain.class, true);
            }
        });
        fadeOut.start();
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivitySplash.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void setupStorageReady() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fadeOutAnimation();
                }
            }, DELAY);
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
            presenter.showError(ex.getMessage());
        }
    }
}
