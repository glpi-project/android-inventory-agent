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

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.flyve.inventory.agent.utils.FlyveLog;

import java.io.InputStream;
import java.util.Properties;

public class AboutActivity extends AppCompatActivity {

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

        try {
            Properties properties = new Properties();
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("about.properties");
            properties.load(inputStream);
            String version = properties.getProperty("about.version");
            String build = properties.getProperty("about.build");
            String date = properties.getProperty("about.date");
            String commit = properties.getProperty("about.commit");
            String commitFull = properties.getProperty("about.commitFull");

            txtAbout.setText(Html.fromHtml(aboutStr(version, build, date, commit, commitFull)));
            txtAbout.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }
    }


    private String aboutStr(String version, String build, String date, String commit, String commitFull) {
        String str = "Inventory Agent, version "+ version +", build "+ build +".<br />";
        str += "Built on "+ date +". Last commit <a href='https://github.com/flyve-mdm/flyve-mdm-ios-inventory-agent/commit/"+commitFull+"'>"+ commit +".<br />";
        str += "© <a href='http://teclib-edition.com/'>Teclib'</a> 2017. Licensed under <a href='https://www.gnu.org/licenses/gpl-3.0.en.html'>GPLv3</a>. <a href='https://flyve-mdm.com/'>Flyve MDM</a>®";

        return str;
    }
}
