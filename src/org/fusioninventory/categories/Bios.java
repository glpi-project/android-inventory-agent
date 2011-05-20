package org.fusioninventory.categories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.fusioninventory.FusionInventory;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
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
		// c.put("MMODEL", "Smartphone");
		c.put("SMODEL", Build.MODEL);

		if (Build.VERSION.SDK_INT > 9) {
			// Mother Board Serial Number
			// Since in 2.3.3 a.k.a gingerbread
			c.put("SSN", Build.SERIAL);
		} else {
			String serial = this.getSerialNumberFromCpuinfo();
			if (!serial.equals("") && !serial.equals("0000000000000000")) {
				c.put("SSN", serial);
			} else {
				// Get IMEI serial number
				TelephonyManager telephonyManager = (TelephonyManager) xCtx
						.getSystemService(Context.TELEPHONY_SERVICE);
				c.put("SSN", telephonyManager.getDeviceId());
			}
		}

		this.add(c);
	}

	private String getSerialNumberFromCpuinfo() {
		String serial = "";
		File f = new File("/proc/cpuinfo");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f), 8 * 1024);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Serial")) {
					FusionInventory.log(this, line, Log.VERBOSE);
					String[] results = line.split(":");
					serial = results[1].trim();
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
		return serial.trim();
	}
}
