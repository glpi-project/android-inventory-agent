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

package org.flyve.inventory.agent.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.DrawerAdapter;
import org.flyve.inventory.agent.utils.FlyveLog;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityMain extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FragmentManager mFragmentManager;
    private ListView lstDrawer;
    private ArrayList<HashMap<String, String>> arrDrawer;
    private HashMap<String, String> selectedItem;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkIfAlreadyhavePermission()) {
            ActivityCompat.requestPermissions(ActivityMain.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        // Setup the DrawerLayout and NavigationView
        mDrawerLayout = findViewById(R.id.drawerLayout);

        lstDrawer = findViewById(R.id.lstNV);
        lstDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawers();
                selectedItem = arrDrawer.get(position);
                loadFragment(selectedItem);
            }
        });

        mFragmentManager = getSupportFragmentManager();

        // Setup Drawer Toggle of the Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.app_name);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        loadListDrawer();

    }

    /**
     * Loads the Fragment
     * @param item
     */
    private void loadFragment(HashMap<String, String> item) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        toolbar.setTitle(item.get("name"));

        // Home
        if (item.get("id").equals("1")) {
            FragmentHome f = new FragmentHome();
            fragmentTransaction.replace(R.id.containerView, f).commit();
            return;
        }

        // About
        if (item.get("id").equals("5")) {
            FragmentAbout f = new FragmentAbout();
            fragmentTransaction.replace(R.id.containerView, f).commit();
            return;
        }

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ActivityMain.this, "Permission denied this app could fail", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * Load the list drawer
     */
    public void loadListDrawer() {

        arrDrawer = new ArrayList<>();

        // Information
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("name", getResources().getString(R.string.drawer_inventory));
        map.put("img", "ic_info");
        arrDrawer.add(map);

        // Help
        map = new HashMap<>();
        map.put("id", "4");
        map.put("name", getResources().getString(R.string.drawer_help));
        map.put("img", "ic_help");
        arrDrawer.add(map);

        // About
        map = new HashMap<>();
        map.put("id", "5");
        map.put("name", getResources().getString(R.string.drawer_about));
        map.put("img", "ic_about");
        arrDrawer.add(map);

        try {
            // lad adapter
            DrawerAdapter adapter = new DrawerAdapter(this, arrDrawer);
            lstDrawer.setAdapter(adapter);

            // Select Information on load //
            selectedItem = arrDrawer.get(0);
            loadFragment(selectedItem);
        } catch(Exception ex) {
            FlyveLog.e(ex.getMessage());
        }
    }
}
