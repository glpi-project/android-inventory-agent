package org.flyve.inventory.agent.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.core.permission.Permission;
import org.flyve.inventory.agent.core.permission.PermissionPresenter;
import org.flyve.inventory.agent.utils.Helpers;

public class PermissionActivity extends AppCompatActivity implements Permission.View {

    Permission.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        presenter = new PermissionPresenter(PermissionActivity.this);

        if(Build.VERSION.SDK_INT < 23) {
            permissionSuccess();
        }
    }

    public void requestPermission(View view) {
        if(Build.VERSION.SDK_INT >= 23) {
            presenter.requestPermission(PermissionActivity.this);
        } else {
            permissionSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    presenter.permissionSuccess();
                } else {
                    presenter.showError(getString(R.string.permission_error_result));
                }
            }
        }
    }

    @Override
    public void showError(String message) {
        Helpers.snackClose(PermissionActivity.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void permissionSuccess() {
        presenter.openMain(PermissionActivity.this);
    }
}
