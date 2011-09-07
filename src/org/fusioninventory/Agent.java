package org.fusioninventory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.fusioninventory.utils.EasySSLSocketFactory;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Agent
    extends Service {

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
    private String lastSendResult = null;

    private ClientConnectionManager clientConnectionManager;
    private HttpContext context;
    private HttpParams params;
    private SchemeRegistry mSchemeRegistry = new SchemeRegistry();

    private FusionInventoryApp mFusionApp = null;

    AlarmManager am;
    private	Calendar cal = Calendar.getInstance();

    private boolean notif = false;

    class IncomingHandler
            extends Handler {
            @Override
                public void handleMessage(Message msg) {

                    Message reply = Message.obtain();

                    Accueil.log(this, "message received " + msg.toString(), Log.INFO);

                    switch (msg.what) {

                        case Agent.MSG_CLIENT_REGISTER:
                            client = msg.replyTo;
                            break;

                        case Agent.MSG_AGENT_STATUS:

                            status_agent = inventory.running ? 1 : 0;
                            reply.what = MSG_AGENT_STATUS;
                            reply.arg1 = status_agent;
                            Accueil.log(this, "URL server = " + mFusionApp.getUrl(), Log.VERBOSE);
                            Accueil.log(this, "shouldAutostart = " + mFusionApp.getShouldAutoStart(), Log.VERBOSE);
                            Accueil.log(this, "mFusionApp = " + mFusionApp.toString(), Log.VERBOSE);

                            try {
                                Accueil.log(this, "message sent " + msg.toString(), Log.INFO);
                                if (client != null) {
                                    client.send(reply);
                                } else {
                                    Accueil.log(this, "No client registered", Log.ERROR);
                                }
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;

                        case Agent.MSG_INVENTORY_START:

                            Accueil.log(this, " received starting inventory task", Log.INFO);

                            if (inventory != null) {

                                if (inventory.running) {

                                    Accueil.log(this, " inventory task is already running ...", Log.WARN);
                                } else {
                                    Accueil.log(this, " inventory task not running ...", Log.INFO);
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

                        case Agent.MSG_INVENTORY_SEND:
                            send_inventory();
                            if (client != null) {
                                reply.what = Agent.MSG_INVENTORY_RESULT;

                                Bundle bXML = new Bundle();
                                bXML.putString("html", lastSendResult);
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

    public class AgentBinder
            extends Binder {
            Agent getService() {
                return Agent.this;
            }
    }

    @Override
        public void onCreate() {

            SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean autoInventory = customSharedPreference.getBoolean("autoStartInventory", false);
            String timeInventory = customSharedPreference.getString("timeInventory", "Week");
            notif = customSharedPreference.getBoolean("notif", false);

            if (autoInventory)
            {
                if (timeInventory.equals("Day"))
                {
                    cal.set(Calendar.HOUR_OF_DAY, 18);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                }
                else if(timeInventory.equals("Week"))
                {
                    cal.set(Calendar.DAY_OF_WEEK, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 18);
                    cal.set(Calendar.MINUTE, 33);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                }
                else if(timeInventory.equals("Month"))
                {
                    cal.set(Calendar.WEEK_OF_MONTH, 1);
                    cal.set(Calendar.DAY_OF_WEEK, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 18);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                }

                am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                setRepeatingAlarm();
            }

            Accueil.log(this, "creating inventory task", Log.INFO);

            mFusionApp = (FusionInventoryApp) getApplication();
            Accueil.log(this, "FusionInventoryApp = " + mFusionApp.toString(), Log.VERBOSE);

            inventory = new InventoryTask(this);

            mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Accueil.class), 0);

            if (notif){
                notification = new Notification();
                notification.icon = R.drawable.icon;

                notification.tickerText = getText(R.string.agent_started).toString();
                updateNotification(getText(R.string.agent_started).toString());

                mNM.notify(NOTIFICATION, notification);
            }

            Handler h = new Handler();
            h.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mNM.cancel(NOTIFICATION);
                }
            }, 1000);


        }

    public void updateNotification(String text) {
         SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
         notif = customSharedPreference.getBoolean("notif", false);

        if (notif){
            notification.setLatestEventInfo(this, getText(R.string.app_name), text, contentIntent);
        }
    }

    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Accueil.log(this, "Received start id " + startId + ": " + intent, Log.INFO);

            // We want this service to continue running until it is explicitly
            // stopped, so return sticky.

            // mNM.cancel(NOTIFICATION);

            return START_STICKY;
        }

    public void start_inventory() {


        inventory.run();

        lastXMLResult = inventory.toXML();

        if (client != null) {
            try {
                client.send(Message.obtain(null, Agent.MSG_INVENTORY_FINISHED));
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void send_inventory() {

        if (lastXMLResult == null) {
            Accueil.log(this, "No XML Inventory ", Log.ERROR);
            Toast.makeText(this, R.string.error_inventory, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, R.string.ok_inventory, Toast.LENGTH_SHORT).show();
        }
        URL url = null;

        try {
            url = new URL(mFusionApp.getUrl());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            Accueil.log(this, "inventory server url is malformed " + e.getLocalizedMessage(), Log.ERROR);
            Toast.makeText(this, "Server adress is malformed", Toast.LENGTH_SHORT).show();
        }

        if (url == null) {
            Accueil.log(this, "No URL found ", Log.ERROR);
            Toast.makeText(this, "Server adress not found", Toast.LENGTH_SHORT).show();
            return;
        }

        mSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // https scheme
        mSchemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

        params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);

        //Send FusionInventory specific user agent
        //TODO get App version from manifest or somewhere else
        HttpProtocolParams.setUserAgent(params, "FusionInventory-Agent-Android_v1.0");

        clientConnectionManager = new SingleClientConnManager(params, mSchemeRegistry);
        context = new BasicHttpContext();

        // ignore that the ssl cert is self signed
        String login = mFusionApp.getCredentialsLogin();
        if (!login.equals("")) {
            Accueil.log(this, "HTTP credentials given : use it if necessary", Log.VERBOSE);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(url.getHost(), AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(mFusionApp.getCredentialsLogin(),
                        mFusionApp.getCredentialsPassword()));
            context.setAttribute("http.auth.credentials-provider", credentialsProvider);
        }

        DefaultHttpClient httpclient = new DefaultHttpClient(clientConnectionManager, params);

        HttpPost post = new HttpPost(url.toExternalForm());
        httpclient.addRequestInterceptor(new HttpRequestInterceptor() {

            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                // TODO Auto-generated method stub


                for( Header h : request.getAllHeaders()) {

                    Accueil.log(this, "HEADER : "+ h.getName() + "=" + h.getValue(), Log.VERBOSE);
                }

            }
        });

        try {
            post.setEntity(new StringEntity(lastXMLResult));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = httpclient.execute(post, context);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Accueil.log(this, "Protocol Exception Error : " + e.getLocalizedMessage(), Log.ERROR);
            Toast.makeText(this, "Server doesn't reply", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Accueil.log(this, "IO error : " + e.getLocalizedMessage(), Log.ERROR);
            Accueil.log(this, "IO error : " + url.toExternalForm(), Log.ERROR);
            Toast.makeText(this, "Server doesn't reply", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (response == null) {
            Accueil.log(this, "No HTTP response ", Log.ERROR);
            Toast.makeText(this, "Server doesn't reply", Toast.LENGTH_SHORT).show();
            return;
        }
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            Accueil.log(this, header.getName() + " -> " + header.getValue(), Log.INFO);
        }
        try {
            InputStream mIS = response.getEntity().getContent();
            //StringBuilder content = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(mIS));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = r.readLine()) != null) {
                //content.append(line);
                Accueil.log(this, line, Log.VERBOSE);
                sb.append(line + "\n");
            }
            this.lastSendResult = sb.toString();

        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(this, "Inventory sent", Toast.LENGTH_SHORT).show();
    }

    @Override
        public void onDestroy() {
            // Cancel the persistent notification.

            SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
            notif = customSharedPreference.getBoolean("notif", false);

            mNM.cancel(NOTIFICATION);

            // Tell the user we stopped.
            if (notif){
                Toast.makeText(this, R.string.agent_stopped, Toast.LENGTH_SHORT).show();
            }
        }

    @Override
        public IBinder onBind(Intent intent) {

            return mMessenger.getBinder();
        }

    public void setRepeatingAlarm() {
        Intent intent = new Intent(this, TimeAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

    }


    // private final IBinder mBinder = new AgentBinder();
}
