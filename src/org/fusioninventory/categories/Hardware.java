package org.fusioninventory.categories;

import java.util.Map;
import java.util.Properties;

import org.fusioninventory.FusionInventory;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

public class Hardware
        extends Categories {

    /**
     * 
     */
    private static final long serialVersionUID = 3528873342443549732L;

    public Hardware(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub
        
        Map<String,String> envs = System.getenv();
        for( String env : envs.keySet()) {
            FusionInventory.log(this, String.format("ENV %s = %s" , env, envs.get(env) ) , Log.VERBOSE);
        }
        
        Properties props = System.getProperties();
        
        for(Object prop: props.keySet() ) {
            FusionInventory.log(this, String.format("PROP %s = %s" , (String)prop, props.get(prop) ) , Log.VERBOSE);
        }
        
        ActivityManager activityManager = (ActivityManager) mCtx.getSystemService(Service.ACTIVITY_SERVICE);
        MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        
        Category c = new Category(mCtx,"HARDWARE");
        
        
        //c.put("ARCHNAME" , Build.DISPLAY);
        c.put("CHECKSUM" , String.valueOf(0xFFFF));
        c.put("DATELASTLOGGEDUSER",String.valueOf(DateFormat.format("MM/dd/yy", Build.TIME)) );
//        this.content.put("DEFAULTGATEWAY","");
//        this.content.put("DESCRIPTION","");
//        this.content.put("DNS","");
//        this.content.put("ETIME","");
//        this.content.put("IPADDR","");
        c.put("LASTLOGGEDUSER",Build.USER);
        c.put("MEMORY", String.valueOf(info.availMem) );
        //c.put("NAME", Build.MANUFACTURER + " " + Build.BOARD);
        c.put("NAME", Build.MODEL);
        c.put("OSCOMMENTS","");
        c.put("OSNAME","Android " + Build.DISPLAY);
        c.put("OSVERSION",Build.VERSION.RELEASE);
        //c.put("PROCESSOR",Build.CPU_ABI + "," + Build.CPU_ABI2);
        //c.put("PROCESSORS","");
        //c.put("PROCESSORT","");
        //c.put("USERDOMAIN","");
        //c.put("USERID","");
        //c.put("UUID","");
        //c.put("VMSYSTEM","");
        //c.put("WORKGROUP","");

        this.add(c);

    }
}
