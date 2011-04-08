package org.fusioninventory.categories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.fusioninventory.FusionInventory;

import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

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
        c.put("MMODEL", Build.BOARD);
        
        
        // Mother Board Serial Number
        // TODO Coming soon in 2.3.3 a.k.a gingerbread
        //c.put("MSN", Build.SERIAL);

        
//        // System Manufacturer
//        c.put("SMANUFACTURER", Build.BRAND);
//        // System Model
//        c.put("SMODEL", Build.MODEL);
//        // System Serial Number
//        c.put("SSN", Build.ID);
  
        File f = new File("/proc/cpuinfo");
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                while ((line = br.readLine()) != null) {
                    //content.append(line);
                    FusionInventory.log(this, line, Log.VERBOSE);
                    //sb.append(line + "\n");
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        this.add(c);
    }

}
