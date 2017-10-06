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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;

import org.flyve.inventory.agent.utils.EnvironmentInfo;

public class AboutActivity extends AppCompatActivity {

    private int countEasterEgg;

    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param Bundle savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.menu_about));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView txtAbout = (TextView) findViewById(R.id.txtAbout);

        ImageView imgInventory = (ImageView) findViewById(R.id.imgInventory);
        imgInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countEasterEgg++;
                if (countEasterEgg > 6 && countEasterEgg <= 10) {
                    Toast.makeText(AboutActivity.this, getResources().getQuantityString(R.plurals.easter_egg_attempts, countEasterEgg, countEasterEgg), Toast.LENGTH_SHORT).show();
                }
                if (countEasterEgg == 10) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AboutActivity.this);
                    Boolean val = sharedPreferences.getBoolean("crashReport",false);

                    if(val) {
                        Bugsnag.notify(new RuntimeException("Easter Egg Fail on" + AboutActivity.this.getResources().getString(R.string.app_name)));
                    } else {
                        Toast.makeText(AboutActivity.this, getResources().getString(R.string.crashreport_disable), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        EnvironmentInfo enviromentInfo = new EnvironmentInfo(AboutActivity.this);

        if(enviromentInfo.getIsLoaded()) {
            txtAbout.setText(Html.fromHtml(aboutStr(enviromentInfo.getVersion(), enviromentInfo.getBuild(), enviromentInfo.getDate(), enviromentInfo.getCommit(), enviromentInfo.getCommitFull(), enviromentInfo.getGithub())));
            txtAbout.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            txtAbout.setVisibility(View.GONE);
        }
    }

    private String aboutStr(String version, String build, String date, String commit, String commitFull, String github) {
        String str = "Inventory Agent, version "+ version +", build "+ build +".<br />";
        str += "Built on "+ date +". Last commit <a href='"+github+"/commit/"+commitFull+"'>"+ commit +"</a>.<br />";
        str += "© <a href='http://teclib-edition.com/'>Teclib'</a> 2017. Licensed under <a href='https://www.gnu.org/licenses/gpl-3.0.en.html'>GPLv3</a>. <a href='https://flyve-mdm.com/'>Flyve MDM</a>®";

        return str;
    }
}
