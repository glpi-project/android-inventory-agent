package org.fusioninventory.categories;

import java.io.File;
import org.fusioninventory.FusionInventory;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
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
    
    /**
     * Add a storage to inventory
     * @param xCtx the Context
     * @param f the partition to inventory
     */
    private void addStorage(Context xCtx, File f) {
        Category c = new Category(xCtx, "DRIVES");
        c.put("VOLUMN", f.toString());

        FusionInventory.log(this, "SDK number :"+Build.VERSION.SDK_INT, Log.VERBOSE);
        
        //Android 2.3.3 or higher
        if(Build.VERSION.SDK_INT > 8) {
        	FusionInventory.log(this, "SDK > 8, use SDK to get total and free disk space", Log.VERBOSE);
        	Long total = f.getTotalSpace();
	        total = total / 1048576;
	      	c.put("TOTAL", total.toString());
	        Long free = f.getFreeSpace();
	        free = free / 1048576;
	      	c.put("FREE", free.toString());
        //Android < 2.3.3
        } else {
        	StatFs stat = new StatFs(f.toString());
        	Integer total = (stat.getBlockCount() * stat.getBlockSize()) / 1048576;
        	c.put("TOTAL", total.toString());
        	Integer free = (stat.getFreeBlocks() * stat.getBlockSize()) / 1048576;
        	c.put("FREE", free.toString());
        	
        }
        this.add(c);
    }

}
