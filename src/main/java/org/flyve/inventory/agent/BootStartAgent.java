package org.flyve.inventory.agent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.flyvemdm.inventory.categories.StringUtils;

public class BootStartAgent extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        // TODO Auto-generated method stub
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        boolean shouldAutoStart = prefs.getBoolean("boot", false);

        FusionInventory.log(this, String.format("Intent %s Category %s",
                intent.getAction(),
                StringUtils.join(intent.getCategories(), " , ")), Log.INFO);

        if (shouldAutoStart) {
            FusionInventory.log(this,
                    "FusionInventory Agent is being started automatically",
                    Log.INFO);
            Intent serviceIntent = new Intent();
            serviceIntent.setAction("org.fusioninventory.Agent");
            ctx.startService(serviceIntent);
        } else {
            FusionInventory.log(this,
                    "FusionInventory Agent will not be started automatically",
                    Log.INFO);
        }
    }
}
