package org.fusioninventory.categories;

import java.io.File;

import org.fusioninventory.FusionInventory;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class Drives extends Categories {
    /**
     * 
     */
    private static final long serialVersionUID = -559572118090134691L;

    public Drives(Context xCtx) {
        super(xCtx);
        
        this.addStorage(xCtx, Environment.getRootDirectory());
        this.addStorage(xCtx, Environment.getExternalStorageDirectory());
        this.addStorage(xCtx, Environment.getDataDirectory());
        this.addStorage(xCtx, Environment.getDownloadCacheDirectory());
    }
    
    private void addStorage(Context xCtx, File f) {
        Category c = new Category(xCtx, "DRIVES");
        c.put("VOLUME", f.toString());

        FusionInventory.log(this, "SDK number :"+Build.VERSION.SDK_INT, Log.VERBOSE);
        if(Build.VERSION.SDK_INT > 8) {
            FusionInventory.log(this, "SDK > 8, use SDK to get total and free disk space", Log.VERBOSE);
            Long total = f.getTotalSpace();
            total = total / 10000;
        	c.put("TOTAL", total.toString());
            Long free = f.getFreeSpace();
            free = free / 10000;
        	c.put("FREE", free.toString());
        } else {
            FusionInventory.log(this, "SDK < 9, use df total and free disk space", Log.VERBOSE);
        	c.put("TOTAL","0");
        	c.put("FREE","0");
        }
    	c.put("FILESYSTEM","");
    	c.put("SERIAL","");
        
        this.add(c);
    }
}
