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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.flyvemdm.inventory.InventoryTask;

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
import org.flyve.inventory.agent.utils.EasySSLSocketFactory;
import org.flyve.inventory.agent.utils.FlyveLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class Agent extends Service {

    public InventoryTask inventory = null;

    private NotificationManager mNM;
    private Notification notification;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private int agentStarted = R.string.agent_started;
    private PendingIntent contentIntent;
    private Messenger client = null;
    private String lastXMLResult = null;
    private String lastSendResult = null;
    private SchemeRegistry mSchemeRegistry = new SchemeRegistry();
    private FusionInventoryApp mFusionApp = null;
    private AlarmManager am;
    private	Calendar cal = Calendar.getInstance();
    private boolean notif = false;

    public static final int MSG_CLIENT_REGISTER = 0;
    public static final int MSG_AGENT_STATUS = 1;
    public static final int MSG_INVENTORY_START = 2;
    public static final int MSG_INVENTORY_PROGRESS = 3;
    public static final int MSG_INVENTORY_FINISHED = 4;
    public static final int MSG_REQUEST_INVENTORY = 5;
    public static final int MSG_INVENTORY_RESULT = 6;
    public static final int MSG_INVENTORY_SEND = 7;

    public static final int STATUS_AGENT_IDLE = 0;
    public static final int STATUS_AGENT_WORKING = 1;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Message reply = Message.obtain();

            FlyveLog.log(this, "message received " + msg.toString(), Log.INFO);

            switch (msg.what) {

                case Agent.MSG_CLIENT_REGISTER:
                    client = msg.replyTo;
                    break;

                case Agent.MSG_AGENT_STATUS:

                    int statusAgent = inventory.running ? 1 : 0;
                    reply.what = MSG_AGENT_STATUS;
                    reply.arg1 = statusAgent;
                    FlyveLog.log(this, "URL server = " + mFusionApp.getUrl(), Log.VERBOSE);
                    FlyveLog.log(this, "shouldAutostart = " + mFusionApp.getShouldAutoStart(), Log.VERBOSE);
                    FlyveLog.log(this, "mFusionApp = " + mFusionApp.toString(), Log.VERBOSE);

                    try {
                        FlyveLog.log(this, "message sent " + msg.toString(), Log.INFO);
                        if (client != null) {
                            client.send(reply);
                        } else {
                            FlyveLog.log(this, "No client registered", Log.ERROR);
                        }
                    } catch (RemoteException e) {
                        FlyveLog.e(e.getMessage());
                    }
                    break;

                case Agent.MSG_INVENTORY_START:
                    FlyveLog.log(this, " received starting inventory task", Log.INFO);

                    if (inventory != null) {

                        if (inventory.running) {
                            FlyveLog.log(this, " Inventory task is already running ...", Log.WARN);
                        } else {
                            FlyveLog.log(this, " Inventory task not running ...", Log.INFO);
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
                            FlyveLog.e(e.getMessage());
                        }
                    }
                    break;

                case Agent.MSG_INVENTORY_SEND:
                    start_inventory();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
        public void onCreate() {

            SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
            boolean autoInventory = customSharedPreference.getBoolean("autoStartInventory", false);
            String timeInventory = customSharedPreference.getString("timeInventory", "Week");
            notif = customSharedPreference.getBoolean("notif", false);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            if (autoInventory) {
                if (timeInventory.equals("Day")) {
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

            FlyveLog.log(this, "creating Inventory task", Log.INFO);

            mFusionApp = (FusionInventoryApp) getApplication();
            FlyveLog.log(this, "FusionInventoryApp = " + mFusionApp.toString(), Log.VERBOSE);

            inventory = new InventoryTask(this, "FusionInventory-Agent-Android_v1.0");

            mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Accueil.class), 0);

            if (notif){
                notification = new Notification();
                notification.icon = R.drawable.icon;

                notification.tickerText = getText(R.string.agent_started).toString();
                updateNotification(getText(R.string.agent_started).toString());

                mNM.notify(agentStarted, notification);
            }

            Handler h = new Handler();
            h.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mNM.cancel(agentStarted);
                }
            }, 1000);
        }

    public void updateNotification(String text) {
         SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
         notif = customSharedPreference.getBoolean("notif", false);

        if (notif){
            // notification.setLatestEventInfo(this, getText(R.string.app_name), text, contentIntent);
            Log.d("Notification", text);
        }
    }

    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            FlyveLog.log(this, "Received start id " + startId + ": " + intent, Log.INFO);

            // We want this service to continue running until it is explicitly
            // stopped, so return sticky.

            return START_STICKY;
        }

    public void start_inventory() {
        inventory.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
                lastXMLResult = data;

                Message reply = Message.obtain();
                send_inventory();
                if (client != null) {
                    reply.what = Agent.MSG_INVENTORY_RESULT;

                    Bundle bXML = new Bundle();
                    bXML.putString("html", lastSendResult);
                    reply.setData(bXML);
                    try {

                        client.send(reply);

                    } catch (RemoteException e) {
                        FlyveLog.e(e.getMessage());
                    }
                }
            }

            @Override
            public void onTaskError(Throwable error) {
                Toast.makeText(Agent.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (client != null) {
            try {
                client.send(Message.obtain(null, Agent.MSG_INVENTORY_FINISHED));
            } catch (RemoteException e) {
                FlyveLog.e(e.getMessage());
            }
        }

    }

    public void send_inventory() {

        if (lastXMLResult == null) {
            FlyveLog.log(this, "No XML Inventory ", Log.ERROR);
            Toast.makeText(this, R.string.error_inventory, Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Toast.makeText(this, R.string.ok_inventory, Toast.LENGTH_SHORT).show();
        }
        URL url = null;

        try {
            url = new URL(mFusionApp.getUrl());
        } catch (MalformedURLException e) {
            FlyveLog.log(this, "Inventory server url is malformed " + e.getLocalizedMessage(), Log.ERROR);
            Toast.makeText(this, "Server adress is malformed", Toast.LENGTH_SHORT).show();
        }

        if (url == null) {
            FlyveLog.log(this, "No URL found ", Log.ERROR);
            Toast.makeText(this, "Server adress not found", Toast.LENGTH_SHORT).show();
            return;
        }

        mSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // https scheme
        mSchemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);

        //Send FusionInventory specific user agent
        //TODO get App version from manifest or somewhere else
        HttpProtocolParams.setUserAgent(params, "Inventory-Agent-Android_v1.0");

        ClientConnectionManager clientConnectionManager = new SingleClientConnManager(params, mSchemeRegistry);
        HttpContext context = new BasicHttpContext();

        // ignore that the ssl cert is self signed
        String login = mFusionApp.getCredentialsLogin();
        if (!login.equals("")) {
            FlyveLog.log(this, "HTTP credentials given : use it if necessary", Log.VERBOSE);
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
                for( Header h : request.getAllHeaders()) {

                    FlyveLog.log(this, "HEADER : "+ h.getName() + "=" + h.getValue(), Log.VERBOSE);
                }

            }
        });

        try {
            post.setEntity(new StringEntity(lastXMLResult));
        } catch (UnsupportedEncodingException e1) {
            FlyveLog.e(e1.getMessage());
        }
        HttpResponse response = null;
        try {
            response = httpclient.execute(post, context);
        } catch (ClientProtocolException e) {
            FlyveLog.log(this, "Protocol Exception Error : " + e.getLocalizedMessage(), Log.ERROR);
            Toast.makeText(this, "Server doesn't reply", Toast.LENGTH_SHORT).show();
            FlyveLog.e(e.getMessage());
        } catch (IOException e) {
            FlyveLog.log(this, "IO error : " + e.getLocalizedMessage(), Log.ERROR);
            FlyveLog.log(this, "IO error : " + url.toExternalForm(), Log.ERROR);
            Toast.makeText(this, "Server doesn't reply", Toast.LENGTH_SHORT).show();
            FlyveLog.e(e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "Unknow exception", Toast.LENGTH_SHORT).show();
            FlyveLog.e(e.getMessage());
        }
        if (response == null) {
            FlyveLog.log(this, "No HTTP response ", Log.ERROR);
            Toast.makeText(this, "Server doesn't reply", Toast.LENGTH_SHORT).show();
            return;
        }
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            FlyveLog.log(this, header.getName() + " -> " + header.getValue(), Log.INFO);
        }
        try {
            InputStream mIS = response.getEntity().getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(mIS));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = r.readLine()) != null) {
                FlyveLog.log(this, line, Log.VERBOSE);
                sb.append(line + "\n");
            }
            this.lastSendResult = sb.toString();

        } catch (Exception e) {
            FlyveLog.e(e.getMessage());
        }

        Toast.makeText(this, "Inventory sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.

        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        notif = customSharedPreference.getBoolean("notif", false);

        mNM.cancel(agentStarted);

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
}
