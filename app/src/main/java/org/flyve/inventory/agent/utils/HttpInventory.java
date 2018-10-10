/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @author    Ivan Del Pino
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-inventory-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

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
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.ui.InventoryAgentApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpInventory {

    private Context appContext;
    private InventoryAgentApp mFusionApp;
    private URL url = null;

    private static Handler uiHandler;

    static {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    private static void runOnUI(Runnable runnable) {
        uiHandler.post(runnable);
    }

    /**
     * Constructor of the class, calls the context of the current class
     * @param Context the context of the class
     */
    public HttpInventory(Context context) {
        this.appContext = context;
        mFusionApp = (InventoryAgentApp) context.getApplicationContext();
    }

    /**
     * Sends the inventory
     * @param string lastXMLResult the inventory information
     * @return boolean true if succeed, false otherwise
     */
    public void sendInventory(final String lastXMLResult, final OnTaskCompleted callback) {

        if (lastXMLResult == null) {
            FlyveLog.log(this, "No XML Inventory ", Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_inventory));
            return;
        }

        try {
            url = new URL(mFusionApp.getUrl());
            FlyveLog.d(url.toString());
        } catch (MalformedURLException e) {
            FlyveLog.log(this, appContext.getResources().getString(R.string.error_url_is_malformed) + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_malformed));
            return;
        }

        if (url == null || "".equals(mFusionApp.getUrl())) {
            FlyveLog.log(this, appContext.getResources().getString(R.string.error_url_is_not_found), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_not_found));
            return;
        }

        if (mFusionApp == null) {
            FlyveLog.log(this, appContext.getResources().getString(R.string.error_send_fail), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_send_fail));
            return;
        }

        Thread t = new Thread(new Runnable() {
            public void run() {
                connect(lastXMLResult, callback);
            }
        });
        t.start();
    }

    private void connect(String lastXMLResult, OnTaskCompleted callback){
        try {
            DataLoader dl = new DataLoader();
            HttpResponse response = dl.secureLoadData(appContext, mFusionApp, lastXMLResult);

            StringBuilder sb = new StringBuilder();
            sb.append("HEADERS:\n\n");

            Header[] headers = response.getAllHeaders();
            for (Header h : headers) {
                sb.append(h.getName()).append(":\t").append(h.getValue()).append("\n");
            }

            InputStream is = response.getEntity().getContent();
            StringBuilder out = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                FlyveLog.log(this, line, Log.VERBOSE);
                out.append(line);
            }
            br.close();

            sb.append("\n\nCONTENT:\n\n").append(out.toString());

            if (sb.toString().toLowerCase().contains("<reply>")) {
                callback.onTaskSuccess(appContext.getResources().getString(R.string.inventory_sent));
            } else {
                if (sb.toString().toLowerCase().contains("404 not found")) {
                    callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_not_found));
                } else {
                    callback.onTaskError(appContext.getResources().getString(R.string.error_server_not_response));
                }
            }

            FlyveLog.d(sb.toString());

        } catch (ClientProtocolException e) {
            FlyveLog.log(this, appContext.getResources().getString(R.string.error_protocol) + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_protocol));
            FlyveLog.e(e.getMessage());
        } catch (IOException e) {
            FlyveLog.log(this, "IO error : " + e.getLocalizedMessage(), Log.ERROR);
            FlyveLog.log(this, "IO error : " + url.toExternalForm(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_server_not_response));
            FlyveLog.e(e.getMessage());
        } catch (Exception e) {
            callback.onTaskError(appContext.getResources().getString(R.string.error_send_fail));
            FlyveLog.e(e.getLocalizedMessage());
        }
    }

    /**
     * This is the interface of return data
     */
    public interface OnTaskCompleted {

        /**
         * if everything is Ok
         * @param data String
         */
        void onTaskSuccess(String data);

        /**
         * if something wrong
         * @param error String
         */
        void onTaskError(String error);

    }
}
