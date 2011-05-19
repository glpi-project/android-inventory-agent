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
        c.put("MEMORY", String.valueOf(info.availMem) );
        c.put("NAME", Build.MODEL);
        c.put("OSCOMMENTS" ,(String)props.get("os.version"));
        c.put("OSNAME", "Android");
        c.put("OSVERSION", Build.VERSION.RELEASE);
        c.put("ARCHNAME", (String)props.getProperty("os.arch"));
        this.add(c);

    }
}
