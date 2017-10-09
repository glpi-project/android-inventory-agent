package org.flyve.inventory.agent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.flyve.inventory.agent.utils.FlyveLog;

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
 * @date      27/9/17
 * @copyright Copyright © 2017 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

public class MainActivity extends AppCompatActivity {

    public static final String FLAG_COMMIT_FRAGMENT = "commitFragment";
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            toolbar.setTitle(getResources().getString(R.string.app_name));
            setSupportActionBar(toolbar);
            getSupportActionBar().setIcon(R.drawable.icon);
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FragmentAccueil()).commit();
    }

    public static Intent getStartIntent(Context context, boolean commitFragment) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(FLAG_COMMIT_FRAGMENT, commitFragment);
        return intent;
    }

    public void loading(Boolean visible) {
        if(visible) {
            pd = ProgressDialog.show(MainActivity.this, "", getResources().getString(R.string.loading));
        } else {
            pd.dismiss();
        }
    }
}
