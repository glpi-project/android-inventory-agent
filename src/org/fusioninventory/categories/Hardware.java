package org.fusioninventory.categories;

import java.util.Properties;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;

public class Hardware
        extends Categories {

    /**
     * 
     */
    private static final long serialVersionUID = 3528873342443549732L;

    public Hardware(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub
        
        Properties props = System.getProperties();
        
        ActivityManager activityManager = (ActivityManager) mCtx.getSystemService(Service.ACTIVITY_SERVICE);
        MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        
        Category c = new Category(mCtx,"HARDWARE");
        
        c.put("CHECKSUM" , String.valueOf(0xFFFF));
        c.put("DATELASTLOGGEDUSER",String.valueOf(DateFormat.format("MM/dd/yy", Build.TIME)) );
        if (!Build.USER.equals(Build.UNKNOWN)) {
            c.put("LASTLOGGEDUSER",Build.USER);
        } else { 
            String user = (String)props.getProperty("user.name");
            if (!user.equals("")) {
                c.put("LASTLOGGEDUSER", (String)props.getProperty("user.name"));
            }
        }

        c.put("NAME", Build.MODEL);
        c.put("OSNAME", "Android "+Build.VERSION.RELEASE);
        c.put("OSVERSION", (String)props.get("os.version"));
        c.put("ARCHNAME", (String)props.getProperty("os.arch"));
        c.put("SDK", new Integer(Build.VERSION.SDK_INT).toString());
        
        //For OCS compatibility
        Memory memory = new Memory(xCtx);
        c.put("MEMORY", memory.getCapacity());

        Cpus cpu = new Cpus(xCtx);
        c.put("PROCESSORT", cpu.getCpuName());
        c.put("PROCESSORS", cpu.getCpuFrequency());
        this.add(c);

    }
}
