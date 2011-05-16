package org.fusioninventory.categories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fusioninventory.FusionInventory;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.content.Context;

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
        
        c.put("CHECKSUM" , String.valueOf(0xFFFF));
        c.put("DATELASTLOGGEDUSER",String.valueOf(DateFormat.format("MM/dd/yy", Build.TIME)) );
        if (!Build.USER.equals(Build.UNKNOWN)) {
            c.put("LASTLOGGEDUSER",Build.USER);
        }
        c.put("MEMORY", String.valueOf(info.availMem) );
        c.put("NAME", Build.MODEL);
        c.put("OSCOMMENTS" ,getFormattedKernelVersion());
        c.put("OSNAME", "Android " + Build.DISPLAY);
        c.put("OSVERSION", Build.VERSION.RELEASE);
                
        this.add(c);

    }
    
    //Copy past from the Settings app
    private String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
            try {
                procVersionStr = reader.readLine();
            } finally {
                reader.close();
            }

            final String PROC_VERSION_REGEX =
                "\\w+\\s+" + /* ignore: Linux */
                "\\w+\\s+" + /* ignore: version */
                "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                "([^\\s]+)\\s+" + /* group 3: #26 */
                "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                //Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
                return "";
            } else if (m.groupCount() < 4) {
                //Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                //        + " groups");
                return "";
            } else {
                return (new StringBuilder(m.group(1)).append(" ")).toString();
            }
        } catch (IOException e) {  
            //Log.e(TAG,
            //    "IO Exception when getting kernel version for Device Info screen",
            //    e);

            return "";
        }
    }
    
}
