package org.fusioninventory.categories;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        c.put("VOLUMN", f.toString());

        FusionInventory.log(this, "SDK number :"+Build.VERSION.SDK_INT, Log.VERBOSE);
        if(Build.VERSION.SDK_INT > 8) {
        	FusionInventory.log(this, "SDK > 8, use SDK to get total and free disk space", Log.VERBOSE);
        	Long total = f.getTotalSpace();
	        total = total / 100000;
	      	c.put("TOTAL", total.toString());
	        Long free = f.getFreeSpace();
	        free = free / 100000;
	      	c.put("FREE", free.toString());
        } else {
        	c = this.getDrivesFromDF(c, f.toString());
        }
        this.add(c);
    }
    
	private Category getDrivesFromDF(Category c, String volume) {
		
		try {
			Process process = Runtime.getRuntime().exec("df " + volume);
			BufferedReader br = new BufferedReader(
		            new InputStreamReader(process.getInputStream()), 8 * 1024);
			String line;
			while ((line = br.readLine()) != null) {
		        String[] data = line.split(":");
		        String[] sizes = data[1].split(",");

		        String total = sizes[0].replaceAll("([0-9].*)K(.*)", "$1");
		        Long totallong = new Long(total.trim());
		        totallong = totallong / 1000;
		      	c.put("TOTAL", totallong.toString());
		        String available = sizes[2].replaceAll("([0-9].*)K(.*)", "$1");
		        Long availablelong = new Long(available.trim());
		        availablelong = availablelong / 1000;
		      	c.put("FREE", availablelong.toString());
			}
			br.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return c;
	}
}
