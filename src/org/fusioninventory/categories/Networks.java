package org.fusioninventory.categories;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Networks
        extends Categories {

    /**
     * 
     */
    private static final long serialVersionUID = 6829495385432791427L;

    public Networks(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub
        
        
        
        ConnectivityManager CM = (ConnectivityManager) mCtx.getSystemService(Service.CONNECTIVITY_SERVICE);
        
        NetworkInfo[] list = CM.getAllNetworkInfo();
        
        for( NetworkInfo e : list ) {
            Category c = new Category(xCtx, "NETWORKS");
            c.put("TYPE", e.getTypeName());
            c.put("SUBTYPE", e.getSubtypeName());
            c.put("STATE",e.getState().toString());

            this.add(c);
        }
        
    }

}
