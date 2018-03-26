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
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.core.help.Help;
import org.flyve.inventory.agent.core.help.HelpPresenter;
import org.flyve.inventory.agent.utils.Helpers;

public class FragmentHelp extends Fragment implements Help.View {

    private static final String HELP_URL = "http://flyve.org/android-inventory-agent/";

    private Help.Presenter presenter;

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

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_help, container, false);

        presenter =  new HelpPresenter(this);

        WebView wv = v.findViewById(R.id.webview);
        presenter.loadWebsite(FragmentHelp.this.getActivity(), wv, HELP_URL);

        return v;
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(FragmentHelp.this.getActivity(), message, getString(R.string.permission_snack_ok), true);
    }
}
