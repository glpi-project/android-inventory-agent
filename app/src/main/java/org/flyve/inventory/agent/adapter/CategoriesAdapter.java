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
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.utils.LocalPreferences;

import java.util.ArrayList;


public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<String> data;
    private Activity activity;
    private LocalPreferences preferences;

    public CategoriesAdapter(ArrayList<String> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int resource = R.layout.list_item_categories;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);
        preferences = new LocalPreferences(activity);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((DataViewHolder) holder).bindData(data.get(position), position);
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox checkShowCategory;
        View viewSeparatorBottom;

        DataViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            checkShowCategory = itemView.findViewById(R.id.checkShowCategory);
            viewSeparatorBottom = itemView.findViewById(R.id.viewSeparatorBottom);
        }

        void bindData(final String model, int position) {
            if (position % 2 == 1) {
                itemView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            } else {
                itemView.setBackgroundColor(activity.getResources().getColor(R.color.grayDarkList));
            }

            if ((data.size() -1) == position) {
                viewSeparatorBottom.setVisibility(View.VISIBLE);
            }

            title.setText(model);
            checkShowCategory.setChecked(preferences.loadCategories().contains(model));
            checkShowCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> categories = preferences.loadCategories();
                    if (categories.contains(model)) {
                        categories.remove(model);
                    } else {
                        categories.add(model);
                    }
                    preferences.saveCategories(categories);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}