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
 * @link      https://github.com/flyvemdm/flyve-mdm-android-inventory-agent
 * @link      http://www.glpi-project.org/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TwoLineListItem;

import org.flyve.inventory.agent.utils.FlyveLog;

public class UrlPreference extends DialogPreference {

    private String url;
    private EditText mEditText;

    public UrlPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.pref_widget_url);
        FlyveLog.log(this, String.format("isPersistent %s", isPersistent()), Log.WARN);
        setPersistent(true);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TwoLineListItem mUrl = (TwoLineListItem) view;

        mUrl.getText1().setText(getTitle());
        mUrl.getText2().setText(getPersistedString(url));
    }

    @Override
    protected View onCreateDialogView() {
        mEditText = new EditText(getContext());
        return mEditText;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        FlyveLog.log(this, "onDialogClosed " + url, Log.WARN);

        if (!positiveResult) {
            return;
        }

        if (callChangeListener(url)) {
            url = mEditText.getText().toString();

            if (shouldPersist()) {
                persistString(url);
            }
            notifyChanged();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        FlyveLog.log(this, "onBindDialogView " + url, Log.WARN);
        mEditText.setText(getPersistedString(url));
        super.onBindDialogView(view);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // This preference type's value type is Integer, so we read the default
        // value from the attributes as an Integer.
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            // Restore state
            url = getPersistedString((String) defaultValue);
        } else {
            // Set state
            String value = (String) defaultValue;
            url = value;
            persistString(value);
        }
    }
}
