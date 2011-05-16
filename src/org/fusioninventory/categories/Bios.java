package org.fusioninventory.categories;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;

public class Bios extends Categories {

    /**
     * 
     */
    private static final long serialVersionUID = -559572118090134691L;

    public Bios(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub
        Category c = new Category(xCtx, "BIOS");

        // Bios Date

        c.put("BDATE", (String) DateFormat.format("MM/dd/yy", Build.TIME));
        // Bios Manufacturer
        c.put("BMANUFACTURER", Build.MANUFACTURER);
        // Bios Revision
        c.put("BVERSION", Build.BOOTLOADER);

        // Mother Board Manufacturer
        c.put("MMANUFACTURER", Build.MANUFACTURER);
        // Mother Board Model 
        //c.put("MMODEL", Build.BOARD);
        c.put("MMODEL", Build.MODEL);
        
        if (Build.VERSION.SDK_INT > 9) {
            // Mother Board Serial Number
            // TODO Coming soon in 2.3.3 a.k.a gingerbread
            c.put("SSN", Build.SERIAL);
        } else {
            //Get IMEI serial number
            TelephonyManager telephonyManager = (TelephonyManager)xCtx.getSystemService(Context.TELEPHONY_SERVICE);
            c.put("SSN", telephonyManager.getDeviceId());
        }
            
        this.add(c);
    }

}
