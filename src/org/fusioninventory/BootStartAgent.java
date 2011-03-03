package org.fusioninventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootStartAgent extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("org.fusioninventory.Agent");
		ctx.startService(serviceIntent);
	}

}
