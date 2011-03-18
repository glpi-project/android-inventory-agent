package org.fusioninventory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class FusionInventory
        extends Activity {

    private Messenger mAgentService = null;

    private TextView log_text = null;
    // private Button start_button = null;
    private String[] STATUS_AGENT = null;
    private boolean isAgentOk = false;

    public static void log(Object obj, String msg, int level) {
        String final_msg = String.format("[%s] %s", obj.getClass().getName(), msg);
        Log.println(level, "FusionInventory", final_msg);
    }

    class IncomingHandler
            extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            FusionInventory.log(this, " message received " + msg.toString(), Log.INFO);

            switch (msg.what) {

            case Agent.MSG_AGENT_STATUS:

                // log_text.setText(STATUS_AGENT[msg.arg1]);
                FusionInventory.log(this, STATUS_AGENT[msg.arg1], Log.INFO);
                // start_button.setEnabled(msg.arg1 == 0 ? true : false);
                isAgentOk = (msg.arg1 == 0 ? true : false);
                break;
            case Agent.MSG_INVENTORY_FINISHED:

                try {
                    mAgentService.send(Message.obtain(null, Agent.MSG_INVENTORY_RESULT));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case Agent.MSG_INVENTORY_RESULT:
                Bundle bXML = msg.peekData();
                if (bXML != null) {
                    log_text.setText(bXML.getString("result"));
                    try {
                        mAgentService.send(Message.obtain(null, Agent.MSG_AGENT_STATUS));
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

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mAgentService = new Messenger(service);

            try {
                Message msg = Message.obtain();
                msg.replyTo = mMessenger;
                msg.what = Agent.MSG_CLIENT_REGISTER;
                mAgentService.send(msg);

                mAgentService.send(Message.obtain(null, Agent.MSG_AGENT_STATUS));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Toast.makeText(FusionInventory.this, R.string.agent_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAgentOk = false;
            mAgentService = null;
            Toast.makeText(FusionInventory.this, R.string.agent_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

    private boolean mIsBound = false;

    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).

        // mIsBound = bindService(new Intent(FusionInventory.this, Agent.class),
        // mConnection, Context.BIND_AUTO_CREATE);

        ComponentName result = startService(new Intent("org.fusioninventory.Agent"));
        if (result != null) {
            FusionInventory.log(this, " Agent started ", Log.INFO);
        } else {
            FusionInventory.log(this, " Agent already started ", Log.ERROR);
        }

        mIsBound = bindService(new Intent(FusionInventory.this, Agent.class), mConnection, Context.BIND_NOT_FOREGROUND);

        if (mIsBound) {
            FusionInventory.log(this, "Connected sucessfully to Agent service", Log.INFO);
        } else {
            FusionInventory.log(this, "Failed to connect to Agent service", Log.ERROR);
        }

    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    /** Called when the activity is first created. */
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        STATUS_AGENT = getResources().getStringArray(R.array.agent_status);

        setContentView(R.layout.main);

        doBindService();

        log_text = (TextView) findViewById(R.id.log_text);
        log_text.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FusionInventory.log(this, "OnDestroy()", Log.INFO);
        doUnbindService();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        doUnbindService();
        FusionInventory.log(this, "OnPause()", Log.INFO);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        doBindService();
        FusionInventory.log(this, "OnResume()", Log.INFO);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuItem mMenu_getinv = menu.findItem(R.id.menu_getinv);
        MenuItem mMenu_send = menu.findItem(R.id.menu_send);

        mMenu_getinv.setEnabled(isAgentOk);
        mMenu_send.setEnabled(isAgentOk);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Message msg;
        // TODO Auto-generated method stub
        switch (item.getItemId()) {

        case R.id.menu_clearlog:
            log_text.setText("");
            return true;

        case R.id.menu_settings:
            Intent settings = new Intent(this, Settings.class);
            startActivity(settings);
            return true;

        case R.id.menu_send:

            if (isAgentOk) {
                msg = Message.obtain(null, Agent.MSG_INVENTORY_SEND);
                msg.replyTo = mMessenger;
                try {
                    mAgentService.send(msg);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;

        case R.id.menu_getinv:
            if (isAgentOk) {
                try {

                    isAgentOk = false;

                    msg = Message.obtain(null, Agent.MSG_INVENTORY_START);
                    msg.replyTo = mMessenger;
                    mAgentService.send(msg);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }

    }
}
