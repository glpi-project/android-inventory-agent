/*
 * Copyright (C) 2017 Teclib'
 *
 * This file is part of Flyve MDM Inventory Agent Android.
 *
 * Flyve MDM Inventory Agent Android is a subproject of Flyve MDM. Flyve MDM is a mobile
 * device management software.
 *
 * Flyve MDM Android is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM Inventory Agent Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez - rafaelje
 * @copyright Copyright (c) 2017 Flyve MDM
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyvemdm/flyve-mdm-android-inventory-agent
 * @link      http://www.glpi-project.org/
 * ------------------------------------------------------------------------------
 */
package org.flyve.inventory.agent;

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
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.flyve.inventory.agent.utils.FlyveLog;

public class FusionInventory
        extends Activity {

    private Messenger mAgentService = null;

    private TextView log_text = null;
    private WebView web_text = null;
    // private Button start_button = null;
    private String[] STATUS_AGENT = null;
    private boolean isAgentOk = false;
    private String barcode = null;
    
    public static void log(Object obj, String msg, int level) {
        String final_msg = String.format("[%s] %s", obj.getClass().getName(), msg);
        Log.println(level, "FusionInventory", final_msg);
    }

    class IncomingHandler
            extends Handler {
        @Override
        public void handleMessage(Message msg) {
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
                    FlyveLog.e(e.getMessage());
                }
                break;

            case Agent.MSG_INVENTORY_RESULT:
                Bundle bXML = msg.peekData();
                if (bXML != null) {
                    log_text.setText(bXML.getString("result"));
                    web_text.loadData(bXML.getString("html"), "text/html", "utf-8");
                    try {
                        mAgentService.send(Message.obtain(null, Agent.MSG_AGENT_STATUS));
                    } catch (RemoteException e) {
                        FlyveLog.e(e.getMessage());
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
                FlyveLog.e(e.getMessage());
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

        web_text = (WebView) findViewById(R.id.web_text);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FusionInventory.log(this, "OnDestroy()", Log.INFO);
        doUnbindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
        FusionInventory.log(this, "OnPause()", Log.INFO);

    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
        FusionInventory.log(this, "OnResume()", Log.INFO);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem mMenu_getinv = menu.findItem(R.id.menu_getinv);
        MenuItem mMenu_send = menu.findItem(R.id.menu_send);

        mMenu_getinv.setEnabled(isAgentOk);
        mMenu_send.setEnabled(isAgentOk);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Message msg;
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
                    FlyveLog.e(e.getMessage());
                }
            }
            return true;

        case R.id.menu_getinv:
            if (isAgentOk) {
                try {

                    isAgentOk = false;

                    msg = Message.obtain(null, Agent.MSG_INVENTORY_START);
                    msg.replyTo = mMessenger;
                    if (barcode != null) {
                        Bundle b = new Bundle();
                        b.putString("BARCODE", barcode);
                        msg.setData(b);
                    }
                    mAgentService.send(msg);
                    
                    
                } catch (RemoteException e) {
                    FlyveLog.e(e.getMessage());
                }
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }

    }


}
