package org.fusioninventory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class Agent extends Service {

	static Integer singleton = null;

	private NotificationManager mNM;

	private Notification notification;

	private PendingIntent contentIntent;

	private Messenger client = null;
	public InventoryTask inventory = null;

	static final int MSG_CLIENT_REGISTER = 0;
	static final int MSG_AGENT_STATUS = 1;
	static final int MSG_INVENTORY_START = 2;
	static final int MSG_INVENTORY_PROGRESS = 3;
	static final int MSG_INVENTORY_FINISHED = 4;
	static final int MSG_REQUEST_INVENTORY = 5;
	static final int MSG_INVENTORY_RESULT = 6;
	static final int MSG_INVENTORY_SEND = 7;
	
	static final int STATUS_AGENT_IDLE = 0;
	static final int STATUS_AGENT_WORKING = 1;

	

	private int status_agent = 0;

	private String lastXMLResult = null;

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			
			Message reply = Message.obtain();
			
			FusionInventory.log(this, "message received " + msg.toString(),
					Log.INFO);

			switch (msg.what) {

			case Agent.MSG_CLIENT_REGISTER:
				client = msg.replyTo;
				break;

			case Agent.MSG_AGENT_STATUS:

				status_agent = inventory.isAlive() ? 1 : 0;
				reply.what = MSG_AGENT_STATUS;
				reply.arg1 = status_agent;

				try {
					FusionInventory.log(this,
							"message sent " + msg.toString(), Log.INFO);
					if (client != null) {
						client.send(reply);
					} else {
						FusionInventory.log(this,
								"No client registered", Log.ERROR);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case Agent.MSG_INVENTORY_START:

				FusionInventory.log(this, " received starting inventory task",
						Log.INFO);

				if (inventory != null) {

					if (inventory.isAlive()) {

						FusionInventory.log(this,
								" inventory task is already running ...",
								Log.WARN);
					} else {
						FusionInventory.log(this,
								" inventory task not running ...", Log.INFO);
						start_inventory();
					}
				}

				break;
			case Agent.MSG_INVENTORY_RESULT:
				if (client != null) {
					reply.what = Agent.MSG_INVENTORY_RESULT;
					
					Bundle bXML = new Bundle();
					bXML.putString("result", lastXMLResult);
					reply.setData(bXML);
					try {
						
						client.send(reply);
						
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	final Messenger mMessenger = new Messenger(new IncomingHandler());

	private int NOTIFICATION = R.string.agent_started;

	public class AgentBinder extends Binder {
		Agent getService() {
			return Agent.this;
		}
	}

	@Override
	public void onCreate() {

		FusionInventory.log(this, "creating inventory task", Log.INFO);
		inventory = new InventoryTask(this.getApplicationContext());

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				FusionInventory.class), 0);

		notification = new Notification();
		notification.icon = R.drawable.fusioninventory;

		// mNM.cancel(NOTIFICATION);
		this.updateNotification(getText(R.string.agent_started).toString());
		// startForeground(NOTIFICATION, notification);

	}

	public void updateNotification(String text) {
		notification.setLatestEventInfo(this, getText(R.string.app_name), text,
				contentIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FusionInventory.log(this, "Received start id " + startId + ": "
				+ intent, Log.INFO);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		// mNM.cancel(NOTIFICATION);

		return START_STICKY;
	}

	public void start_inventory() {
		inventory.run();
		try {
			inventory.join();
			SystemClock.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lastXMLResult = inventory.toXML();
		
		if(client!=null) {
			try {
				client.send(Message.obtain(null,Agent.MSG_INVENTORY_FINISHED));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
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

		return mMessenger.getBinder();
	}

	// private final IBinder mBinder = new AgentBinder();
}
