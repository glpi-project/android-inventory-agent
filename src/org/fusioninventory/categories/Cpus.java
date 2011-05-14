package org.fusioninventory.categories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import android.content.Context;


public class Cpus extends Categories {


    /**
     * 
     */
    private static final long serialVersionUID = 4846706700566208666L;

    public Cpus(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub

        Category c = new Category(mCtx, "CPUS");

        File f = new File("/proc/cpuinfo");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String infos = br.readLine();
            c.put("NAME", infos.replaceAll("(.*):\\ (.*)", "$2"));
            c.put("CORE", "1");
            c.put("MANUFACTURER", "ARM");
            c.put("SPEED", "");
            c.put("THREAD", "");
            br.close();
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
