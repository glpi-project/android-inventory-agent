package org.fusioninventory.categories;

import java.util.List;

import org.fusioninventory.FusionInventory;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.util.Log;

public class Softwares
        extends Categories {

    /**
     * 
     */
    private static final long serialVersionUID = 4846706700566208666L;

    public Softwares(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub
        PackageManager PM = mCtx.getPackageManager();

        List<ApplicationInfo> packages = PM.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo p : packages) {
            FusionInventory.log(this, "SOFTWARES " + p.packageName, Log.VERBOSE);

            Category c = new Category(mCtx, "SOFTWARES");
            try {
                PackageInfo pi = PM.getPackageInfo(p.packageName, PackageManager.GET_META_DATA);
                c.put("VERSION", pi.versionName);

            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            PackageStats stats = new PackageStats(p.packageName);
            //c.put("NAME", p.packageName);
            if (p.name != null) {
                c.put("NAME", p.name);
            } else if (p.className != null) {
                c.put("NAME", p.className);
            } else if (p.packageName != null) {
                c.put("NAME", p.packageName);
            }
            
            Bundle b = p.metaData;
            for (String bname : b.keySet()) {
                FusionInventory.log(this, bname + " " + String.valueOf(b.get(bname)),Log.WARN);
            }
            //            c.put("VERSION", p.);
            //            //c.put("INSTALLDATE", stats.)
            c.put("FILESIZE", String.valueOf(stats.cacheSize + stats.codeSize + stats.dataSize));
            c.put("FROM", "apk");
            this.add(c);
        }
    }

}
