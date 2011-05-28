package org.fusioninventory.categories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;

public class Memory extends Categories {

	/**
     * 
     */
	private static final long serialVersionUID = -559572118090134691L;

	public Memory(Context xCtx) {
		super(xCtx);
		// TODO Auto-generated constructor stub
		Category c = new Category(xCtx, "MEMORIES");

        File f = new File("/proc/meminfo");
        try {
            c.put("DESCRIPTION", "Memory");

        	BufferedReader br = new BufferedReader(new FileReader(f), 8 * 1024);
        	String line;
			while ((line = br.readLine()) != null) {
        		if (line.startsWith("MemTotal")) {
                    String[] parts = line.split(":");
                    String part1 = parts[1].trim();
                    Long memory = new Long(part1.replaceAll("(.*)\\ kB", "$1"));
                    memory = memory / 1024;
                    c.put("CAPACITY", String.valueOf(memory));
        		}
        	}

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
