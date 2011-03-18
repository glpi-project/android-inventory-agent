package org.fusioninventory;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TwoLineListItem;

public class UrlPreference
        extends DialogPreference {

    private String url;
    private TwoLineListItem mUrl;
    private EditText mEditText;

    public UrlPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.pref_widget_url);
        FusionInventory.log(this, String.format("isPersistent %s", isPersistent()), Log.WARN);
        setPersistent(true);
    }

    @Override
    protected void onBindView(View view) {
        // TODO Auto-generated method stub
        super.onBindView(view);

        mUrl = (TwoLineListItem) view;

        mUrl.getText1().setText(getTitle());
        mUrl.getText2().setText(getPersistedString(url));
    }

    @Override
    protected View onCreateDialogView() {
        // TODO Auto-generated method stub

        mEditText = new EditText(getContext());
        return mEditText;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        // TODO Auto-generated method stub
        FusionInventory.log(this, "onDialogClosed " + url, Log.WARN);

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
        // TODO Auto-generated method stub
        FusionInventory.log(this, "onBindDialogView " + url, Log.WARN);
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
    // @Override
    // protected void onClick() {
    // super.onClick();
    // FusionInventory.log(this, "Test Click prefs", Log.WARN);
    // // TODO Auto-generated method stub
    // if (!callChangeListener(url)) {
    // FusionInventory.log(this, "Cancel", Log.WARN);
    // return;
    // }
    //
    // persistString(url);
    //
    // notifyChanged();
    // }

}
