package org.flyve.inventory.agent.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.flyve.inventory.agent.R;
import java.util.ArrayList;
import java.util.HashMap;


public class InventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<HashMap<String, String>> data;
    private Activity activity;

    private static final int ITEM_TYPE_DATA = 0;
    private static final int ITEM_TYPE_HEADER = 1;

    public InventoryAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
        this.data = data;
        this.activity = activity;
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

        if(itemType == ITEM_TYPE_DATA) {
            ((DataViewHolder)holder).bindData( response );
        }

        if(itemType == ITEM_TYPE_HEADER) {
            ((HeaderViewHolder)holder).bindData( response );
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        DataViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
        }

        public void bindData(HashMap<String, String> model) {
            title.setText( Html.fromHtml( model.get("title") ));
            description.setText( Html.fromHtml( model.get("description") ));
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
        }

        public void bindData(HashMap<String, String> model) {
            title.setText( Html.fromHtml( model.get("title") ));
        }
    }


}