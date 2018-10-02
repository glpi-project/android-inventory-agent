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

package org.flyve.inventory.agent.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;


public class InventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<HashMap<String, String>> data;

    private static final int ITEM_TYPE_DATA = 0;
    private static final int ITEM_TYPE_HEADER = 1;

    public InventoryAdapter(ArrayList<HashMap<String, String>> data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if((data.get(position)).get("type").equals("header")) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_DATA;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Vista por defecto
        if(viewType == ITEM_TYPE_DATA) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_inventory, viewGroup, false);
            return new DataViewHolder(v);
        }

        // Vista alternativa
        else if (viewType == ITEM_TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_inventory_header, viewGroup, false);
            return new HeaderViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> response = data.get(position);

        final int itemType = getItemViewType(position);

        if (itemType == ITEM_TYPE_DATA) {
            ((DataViewHolder) holder).bindData(response);
        }

        if (itemType == ITEM_TYPE_HEADER) {
            ((HeaderViewHolder) holder).bindData(response);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        DataViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

        void bindData(HashMap<String, String> model) {
            title.setText( Html.fromHtml( Helpers.splitCamelCase(model.get("title")) ));
            description.setText( Html.fromHtml( model.get("description") ));
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }

        void bindData(HashMap<String, String> model) {
            title.setText( Html.fromHtml( model.get("title") ));
        }
    }


}