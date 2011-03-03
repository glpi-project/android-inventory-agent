package org.fusioninventory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Agent extends Service {

	private NotificationManager mNM;

	private Notification notification;
	private PendingIntent contentIntent;
	public InventoryTask inventory = null;

	private int NOTIFICATION = R.string.agent_started;

	private static final String TAG = "FusionInventory Agent";

	public class AgentBinder extends Binder {
		Agent getService() {
			return Agent.this;
		}
	}

	@Override
	public void onCreate() {
		inventory = new InventoryTask(this.getApplicationContext());

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				FusionInventory.class), 0);

		notification = new Notification();
		notification.icon = R.drawable.fusioninventory;
		
		//mNM.cancel(NOTIFICATION);
		this.updateNotification(getText(R.string.agent_started).toString());		
		//startForeground(NOTIFICATION, notification);
		
		
	}

	public void updateNotification(String text) {
		notification.setLatestEventInfo(this, getText(R.string.app_name), text,
				contentIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		
		
		inventory.execute();
		
		
		//mNM.cancel(NOTIFICATION);
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.

		mNM.cancel(NOTIFICATION);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.agent_stopped, Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	private final IBinder mBinder = new AgentBinder();
}
