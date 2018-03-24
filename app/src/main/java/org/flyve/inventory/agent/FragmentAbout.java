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

package org.flyve.inventory.agent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsnag.android.Bugsnag;

import org.flyve.inventory.agent.utils.EnvironmentInfo;

public class FragmentAbout extends Fragment {

    private int countEasterEgg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about, null);

        TextView txtAbout = v.findViewById(R.id.txtAbout);

        ImageView imgInventory = v.findViewById(R.id.imgInventory);
        imgInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countEasterEgg++;
                if (countEasterEgg > 6 && countEasterEgg <= 10) {
                    Toast.makeText(FragmentAbout.this.getContext(), getResources().getQuantityString(R.plurals.easter_egg_attempts, countEasterEgg, countEasterEgg), Toast.LENGTH_SHORT).show();
                }
                if (countEasterEgg == 10) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FragmentAbout.this.getContext());
                    Boolean val = sharedPreferences.getBoolean("crashReport",false);

                    if(val) {
                        Bugsnag.notify(new RuntimeException("Easter Egg Fail on" + FragmentAbout.this.getResources().getString(R.string.app_name)));
                    } else {
                        Toast.makeText(FragmentAbout.this.getContext(), getResources().getString(R.string.crashreport_disable), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        EnvironmentInfo enviromentInfo = new EnvironmentInfo(FragmentAbout.this.getContext());

        if(enviromentInfo.getIsLoaded()) {
            txtAbout.setText(Html.fromHtml(aboutStr(enviromentInfo.getVersion(), enviromentInfo.getBuild(), enviromentInfo.getDate(), enviromentInfo.getCommit(), enviromentInfo.getCommitFull(), enviromentInfo.getGithub())));
            txtAbout.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            txtAbout.setVisibility(View.GONE);
        }

        return v;
    }

    private String aboutStr(String version, String build, String date, String commit, String commitFull, String github) {
        String str = "Inventory Agent, version "+ version +", build "+ build +".<br />";
        str += "Built on "+ date +". Last commit <a href='"+github+"/commit/"+commitFull+"'>"+ commit +"</a>.<br />";
        str += "© <a href='http://teclib-edition.com/'>Teclib'</a> 2017. Licensed under <a href='https://www.gnu.org/licenses/gpl-3.0.en.html'>GPLv3</a>. <a href='https://flyve-mdm.com/'>Flyve MDM</a>®";

        return str;
    }
}
