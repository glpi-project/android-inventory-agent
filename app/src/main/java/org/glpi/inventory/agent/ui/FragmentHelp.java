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

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.core.help.Help;
import org.glpi.inventory.agent.core.help.HelpPresenter;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;

import java.util.List;

public class FragmentHelp extends Fragment implements Help.View {

    private static final String HELP_URL = "https://glpi-project.org/subscriptions/";

    private Help.Presenter presenter;
    private Toolbar toolbar;


    /**
     * Instantiate the user interface View
     * @param inflater inflater the object that can be used to inflate any views in the fragment
     * @param container the parent View the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state
     * @return View the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        toolbar.setTitle(getActivity().getResources().getString(R.string.drawer_help));
        try {
            setRetainInstance(true);

            // Inflate the layout for this fragment
            View v =  inflater.inflate(R.layout.fragment_help, container, false);

            presenter =  new HelpPresenter(this);

            WebView wv = v.findViewById(R.id.webview);
            presenter.loadWebsite(FragmentHelp.this.getActivity(), wv, HELP_URL);

            return v;
        }catch (Exception ex){
            final String appPackageName = "com.google.android.webview";
            if(isPackageExisted(appPackageName)){
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.install_web_view), Toast.LENGTH_LONG).show();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.load_help_error), Toast.LENGTH_LONG).show();
            }
        }

        return null;
    }

    private boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getContext().getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    @Override
    public void setToolbar(Toolbar toolbar){
        this.toolbar = toolbar;
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(FragmentHelp.this.getActivity(), message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public boolean onBackPressed() {
        try {
            FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
            fm.popBackStack ("help", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }catch(NullPointerException ex) {
            AgentLog.e(ex.getMessage());
        }
        return true;
    }
}
