package org.fusioninventory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FusionInventory extends Activity {

	private Agent mBoundService = null;
	private static final String TAG = "FusionInventory Activity";

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((Agent.AgentBinder) service).getService();
			//mBoundService = new Messenger(service);
			Toast.makeText(FusionInventory.this, R.string.agent_connected,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
			Toast.makeText(FusionInventory.this, R.string.agent_disconnected,
					Toast.LENGTH_SHORT).show();
		}
	};

	private boolean mIsBound = false;

	private Button start_button;
	private TextView text_view;

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).

		mIsBound = bindService(new Intent(FusionInventory.this, Agent.class),
				mConnection, Context.BIND_AUTO_CREATE);
		if (mIsBound) {
			start_button.setClickable(true);
			start_button.setEnabled(true);
			Log.i(TAG, "Connected sucessfully to Agent service");
		} else {
			start_button.setClickable(false);
			start_button.setEnabled(false);
			Log.e(TAG, "Failed to connect to Agent service");
		}

	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			start_button.setClickable(false);
			start_button.setEnabled(false);
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		start_button = (Button) findViewById(R.id.button1);
		text_view = (TextView) findViewById(R.id.text_result);
		start_button.setClickable(false);
		
		

		doBindService();
		start_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	    doUnbindService();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doBindService();
	}
}
