package org.flyve.inventory.agent.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.flyve.inventory.agent.R;

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
 * @date      2/10/17
 * @copyright Copyright © 2017 Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */
public class Helpers {

    /**
     * private constructor
     */
    private Helpers() {
    }

    /**
     * Generate a snackbar with the given arguments
     * @param Activity the view to show
     * @param string the message to display
     * @param string the text to display for the action
     * @param View.OnClickListener the callback to be invoked when the action is clicked
     */
    public static void snackClose(Activity activity, String message, String action, Boolean fail) {

        int color = activity.getResources().getColor(R.color.snackbar_action_good);
        if(fail) {
            color = activity.getResources().getColor(R.color.snackbar_action_fail);
        }

        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(color)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }
}
