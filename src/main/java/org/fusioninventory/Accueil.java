package org.fusioninventory;

import java.util.Iterator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class Accueil extends PreferenceActivity implements
OnSharedPreferenceChangeListener {

    private Messenger mAgentService = null;

    private String[] STATUS_AGENT = null;
    private boolean isAgentOk = false;
    private String barcode = null;

    private boolean notif = false;

    private boolean ssh = false;

    private SharedPreferences customSharedPreference;

    private static final String TAG = "DroidSSHd";
    final Handler mHandler = new Handler();
    private long mUpdateUIdelay = 500L;
    private Intent mDropbearDaemonHandlerService;

    public Messenger getmAgentService(){
        return mAgentService;
    }

    public static void log(Object obj, String msg, int level) {
        String final_msg = String.format("[%s] %s", obj.getClass().getName(), msg);
        Log.println(level, "FusionInventory", final_msg);
    }

    class IncomingHandler
            extends Handler {
            @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    Accueil.log(this, " message received " + msg.toString(), Log.INFO);

                    switch (msg.what) {

                        case Agent.MSG_AGENT_STATUS:

                            Accueil.log(this, STATUS_AGENT[msg.arg1], Log.INFO);
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

                if (notif){
                    Toast.makeText(Accueil.this, R.string.agent_connected, Toast.LENGTH_SHORT).show();
                }
            }

        @Override
            public void onServiceDisconnected(ComponentName name) {
                isAgentOk = false;
                mAgentService = null;

                if (notif){
                    Toast.makeText(Accueil.this, R.string.agent_disconnected, Toast.LENGTH_SHORT).show();
                }
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
            Accueil.log(this, " Agent started ", Log.INFO);
        } else {
            Accueil.log(this, " Agent already started ", Log.ERROR);
        }

        mIsBound = bindService(new Intent(Accueil.this, Agent.class), mConnection, Context.BIND_NOT_FOREGROUND);

        if (mIsBound) {
            Accueil.log(this, "Connected sucessfully to Agent service", Log.INFO);
        } else {
            Accueil.log(this, "Failed to connect to Agent service", Log.ERROR);
        }

    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            STATUS_AGENT = getResources().getStringArray(R.array.agent_status);

            addPreferencesFromResource(R.xml.accueil);

            doBindService();

            customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
            notif = customSharedPreference.getBoolean("notif", false);

            Preference autoStartInventory = findPreference("autoStartInventory");
            autoStartInventory.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference arg0,
                    Object arg1) {
                    // TODO Auto-generated method stub

                    notif = customSharedPreference.getBoolean("notif", false);

                    if (notif){
                        Toast.makeText(getBaseContext(), R.string.agent_reboot, Toast.LENGTH_SHORT).show();
                    }

                    stopService(new Intent("org.fusioninventory.Agent"));
                    startService(new Intent("org.fusioninventory.Agent"));

                    return true;
                }

            });

            Preference timeInventory = findPreference("timeInventory");
            timeInventory.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference arg0,
                    Object arg1) {
                    // TODO Auto-generated method stub

                    notif = customSharedPreference.getBoolean("notif", false);

                    if (notif){
                        Toast.makeText(getBaseContext(), R.string.agent_reboot,Toast.LENGTH_SHORT).show();
                    }

                    stopService(new Intent("org.fusioninventory.Agent"));
                    startService(new Intent("org.fusioninventory.Agent"));

                    return true;
                }

            });

            Preference runInventory = findPreference("runInventory");
            runInventory.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Message msg;

                    if (isAgentOk) {
                        try {

                            Toast.makeText(Accueil.this, R.string.inventory_started, Toast.LENGTH_LONG).show();

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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        isAgentOk = true;
                    }

                    if (isAgentOk) {

                        isAgentOk = false;

                        msg = Message.obtain(null, Agent.MSG_INVENTORY_SEND);
                        msg.replyTo = mMessenger;
                        try {
                            mAgentService.send(msg);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        isAgentOk = true;
                    }

                    return true;
                }

            });
        }

    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference editextp = (EditTextPreference) pref;
            pref.setSummary(editextp.getText());
        }
        if (pref instanceof ListPreference) {
            ListPreference listp = (ListPreference) pref;
            pref.setSummary(listp.getValue());
        }
    }
}
