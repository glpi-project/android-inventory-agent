/**
 *  LICENSE
 *
 *  This file is part of Flyve MDM Inventory Agent for Android.
 * 
 *  Inventory Agent for Android is a subproject of Flyve MDM.
 *  Flyve MDM is a mobile device management software.
 *
 *  Flyve MDM is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  Flyve MDM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @copyright Copyright © 2018 Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/flyve-mdm/android-inventory-agent
 *  @link      https://flyve-mdm.com
 *  @link      http://flyve.org/android-inventory-agent
 *  ---------------------------------------------------------------------
 */

package org.flyve.inventory.agent.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.InventoryAdapter;
import org.flyve.inventory.agent.schema.ListInventorySchema;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class FragmentInventoryList extends Fragment {

    private String data;
    private String key;

    public static FragmentInventoryList newInstance(String data, String key) {
        FragmentInventoryList fragmentFirst = new FragmentInventoryList();
        Bundle args = new Bundle();
        args.putString("data", data);
        args.putString("key", key);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    /**
     * Instantiate the user interface View
     * @param inflater inflater the object that can be used to inflate any views in the fragment
     * @param container the parent View the fragment's UI should be attached to
     * @param savedInstanceState this fragment is being re-constructed from a previous saved state
     * @return View the View for the fragment's UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            data = getArguments().getString("data");
            key = getArguments().getString("key");
        }
        return inflater.inflate(R.layout.fragment_inventory_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView lst = view.findViewById(R.id.lst);
        lst.setLayoutManager(new LinearLayoutManager(getActivity()));
        lst.setAdapter(new InventoryAdapter(load(), requireActivity()));
        lst.setNestedScrollingEnabled(false);
    }

    private ArrayList<ArrayList<ListInventorySchema>> load() {
        ArrayList<ArrayList<ListInventorySchema>> dataList = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(data);
            JSONObject jsonRequest = json.getJSONObject("request");
            JSONObject jsonContent = jsonRequest.getJSONObject("content");

            Iterator<?> keys = jsonContent.keys();

            while( keys.hasNext() ) {
                String key = (String) keys.next();

                if (key.equalsIgnoreCase(this.key)) {
                    if (jsonContent.get(key) instanceof JSONArray) {
                        if (!key.equals("")) {
                            JSONArray category = jsonContent.getJSONArray(key);
                            for (int y = 0; y < category.length(); y++) {
                                ArrayList<ListInventorySchema> list = new ArrayList<>();
                                JSONObject obj = category.getJSONObject(y);
                                Iterator<?> keysObj = obj.keys();
                                while (keysObj.hasNext()) {
                                    String keyObj = (String) keysObj.next();
                                    ListInventorySchema listInventory = new ListInventorySchema();
                                    listInventory.setTitle(keyObj);
                                    listInventory.setType("data");
                                    listInventory.setDescription(obj.getString(keyObj));
                                    list.add(listInventory);
                                    FlyveLog.d(keyObj);
                                }
                                dataList.add(list);
                            }
                        }
                    }
                    break;
                }
            }
            return dataList;
        } catch (Exception ex) {
            FlyveLog.e(ex.getMessage());
        }
        return dataList;
    }
}
