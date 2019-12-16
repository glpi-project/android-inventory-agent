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

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.adapter.InventoryAdapter;
import org.glpi.inventory.agent.schema.ListInventorySchema;
import org.glpi.inventory.agent.utils.AgentLog;
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
                                    AgentLog.d(keyObj);
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
            AgentLog.e(ex.getMessage());
        }
        return dataList;
    }
}
