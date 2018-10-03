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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.adapter.InventoryAdapter;
import org.flyve.inventory.agent.core.report.Report;
import org.flyve.inventory.agent.core.report.ReportPresenter;
import org.flyve.inventory.agent.utils.FlyveLog;
import org.flyve.inventory.agent.utils.Helpers;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FragmentInventoryList extends Fragment implements Report.View {

    private String data;
    private String key;

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

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

        Report.Presenter presenter = new ReportPresenter(this);

        final RecyclerView lst = view.findViewById(R.id.lst);
        lst.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (recyclerViewReadyCallback != null) {
                    recyclerViewReadyCallback.onLayoutReady();
                }
                lst.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        lst.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        InventoryAdapter mAdapter = new InventoryAdapter(load());
        lst.setAdapter(mAdapter);
        String message = requireActivity().getResources().getString(R.string.loading);
        final ProgressDialog progressBar = ProgressDialog.show(requireActivity(), "Creating inventory", message);

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                progressBar.dismiss();
            }
        };

        presenter.generateReport(getActivity());
    }

    private ArrayList<HashMap<String, String>> load() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(data);
            JSONObject jsonRequest = json.getJSONObject("request");
            JSONObject jsonContent = jsonRequest.getJSONObject("content");

            Iterator<?> keys = jsonContent.keys();

            while( keys.hasNext() ) {
                String key = (String) keys.next();

                if (key.equalsIgnoreCase(this.key)) {
                    if (jsonContent.get(key) instanceof JSONArray) {
                        // add header
                        FlyveLog.d("----------- Header: " + key);

                        HashMap<String, String> h = new HashMap<>();
                        h.put("type", "header");
                        h.put("title", key.toUpperCase());

                        if (!key.trim().equals("")) {
                            dataList.add(h);
                        }

                        if (!key.equals("")) {
                            JSONArray category = jsonContent.getJSONArray(key);
                            for (int y = 0; y < category.length(); y++) {
                                JSONObject obj = category.getJSONObject(y);
                                Iterator<?> keysObj = obj.keys();
                                while (keysObj.hasNext()) {
                                    HashMap<String, String> c = new HashMap<>();
                                    String keyObj = (String) keysObj.next();
                                    c.put("type", "data");
                                    c.put("title", keyObj);
                                    c.put("description", obj.getString(keyObj));
                                    FlyveLog.d(keyObj);
                                    dataList.add(c);
                                }
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

    @Override
    public void showError(String message) {
        Helpers.snackClose(getActivity(), message, getString(R.string.permission_snack_ok), true);
    }

    @Override
    public void sendInventory(String data, ArrayList<String> load) {

    }
}
