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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.flyve.inventory.InventoryTask;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.adapter.ViewPagerAdapter;
import org.glpi.inventory.agent.core.report.Report;
import org.glpi.inventory.agent.core.report.ReportPresenter;
import org.glpi.inventory.agent.utils.AgentLog;
import org.glpi.inventory.agent.utils.Helpers;
import org.glpi.inventory.agent.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class ActivityInventoryReport extends AppCompatActivity implements Report.View {

    private Report.Presenter presenter;
    private ProgressBar progressBar;
    private FloatingActionButton btnShare;
    private SharedPreferences sharedPreferences;

    private ActivityResultLauncher<Intent> saveFileLauncher;
    private String fileContentToSave;


    /**
     * Called when the activity is starting, inflates the activity's UI
     * @param savedInstanceState if the activity is re-initialized, it contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        saveFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                                if (out != null) {
                                    out.write(fileContentToSave.getBytes(StandardCharsets.UTF_8));
                                }
                                Toast.makeText(this, R.string.file_saved_successfully, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, R.string.file_save_failed, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );

        ActivityCompat.requestPermissions(ActivityInventoryReport.this,
                new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                },
                1);

        setContentView(R.layout.activity_inventory);
        progressBar = findViewById(R.id.progressBar);
        presenter = new ReportPresenter(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
        }

        btnShare = findViewById(R.id.btnShare);
        btnShare.hide();

        progressBar.setVisibility(View.VISIBLE);
        presenter.generateReport(ActivityInventoryReport.this);

        btnShare.setOnClickListener(v -> {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(sharedPreferences.getBoolean("noRemindShareWarning",false)) {
                showDialogShare();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityInventoryReport.this);
                builder.setTitle(getApplication().getResources().getString(R.string.share_warning));
                builder.setIcon(R.drawable.ic_launcher_round);
                builder.setMessage(R.string.share_message);
                builder.setPositiveButton(R.string.share_understand, (parentDialog, id) -> showDialogShare());

                builder.setNeutralButton(R.string.share_no_reminde_me, (parentDialog, id) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("noRemindShareWarning", true);
                    editor.apply();
                    showDialogShare();
                });

                builder.setNegativeButton(R.string.share_cancel, (dialog, id) -> dialog.cancel());
                builder.create().show();
            }
        });
    }

    private void showDialogShare(){
        final String[] shareItems = getResources().getStringArray(R.array.export_list);
        presenter.showDialogShare(ActivityInventoryReport.this, shareItems, (index) -> {
            File file;

            if (index == 1) {
                file = new File(getFilesDir(), "Inventory.json");
                if (!file.exists()) {
                    AgentLog.e("JSON File not exist");
                }
            }
            else {
                file = new File(getFilesDir(), "Inventory.xml");
                if (!file.exists()) {
                    AgentLog.e("XML File not exist");
                }
            }


            String content = Helpers.readFileContent(file);
            String mimeType = index == 1 ? "application/json" : "application/xml";
            String filename = index == 1 ? "Inventory.json" : "Inventory.xml";

            launchSaveFileIntent(filename, mimeType, content);
        });
    }

    private void launchSaveFileIntent(String fileName, String mimeType, String content) {
        this.fileContentToSave = content;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        saveFileLauncher.launch(intent);
    }


    @Override
    public void showError(String message) {
        Helpers.snackClose(ActivityInventoryReport.this, message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void sendInventory(String data, ArrayList<String> load) {
        try {
            ArrayList<String> list = new ArrayList<>();
            Collections.addAll(list, Utils.getFormatTitle(load));

            ArrayList<String> listTitle = new ArrayList<>();
            for (String value : list) {
                String resource = Utils.getStringResourceByName(value, this);
                listTitle.add(!resource.equals("") ? resource : value);
            }
            ViewPager viewPager = findViewById(R.id.viewPager);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), data, load, listTitle, progressBar);
            viewPager.setAdapter(viewPagerAdapter);
            TabLayout tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            btnShare.show();
        } catch (Exception ex) {
            AgentLog.e(ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    String message = getResources().getString(R.string.permission_error_result);
                    Helpers.snackClose(ActivityInventoryReport.this, message, getString(R.string.permission_snack_ok), true);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //FIX: TransactionTooLargeException when sharing Uri via intent
        outState.clear();
    }
}
