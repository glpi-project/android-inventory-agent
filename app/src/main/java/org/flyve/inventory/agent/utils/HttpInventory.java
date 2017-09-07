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
package org.flyve.inventory.agent.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import org.flyve.inventory.agent.InventoryAgentApp;
import org.flyve.inventory.agent.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpInventory {

    private Context context;
    private InventoryAgentApp mFusionApp;

    /**
     * Constructor of the class, calls the context of the current class
     * @param Context the context of the class
     */
    public HttpInventory(Context context) {
        this.context = context;
        mFusionApp = (InventoryAgentApp) context.getApplicationContext();
    }

    /**
     * Sends the inventory
     * @param string lastXMLResult the inventory information
     * @return boolean true if succeed, false otherwise
     */
    public Boolean sendInventory(String lastXMLResult, OnTaskCompleted callback) {

        if (lastXMLResult == null) {
            FlyveLog.log(this, "No XML Inventory ", Log.ERROR);
            callback.onTaskError(context.getResources().getString(R.string.error_inventory));
            return false;
        }

        URL url = null;

        try {
            url = new URL(mFusionApp.getUrl());
            FlyveLog.d(url.toString());
        } catch (MalformedURLException e) {
            FlyveLog.log(this, "Inventory server url is malformed " + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError("Inventory server url is malformed");
            return false;
        }

        if (url == null) {
            FlyveLog.log(this, "No URL found ", Log.ERROR);
            callback.onTaskError("Server adress not found");
            return false;
        }

        SchemeRegistry mSchemeRegistry = new SchemeRegistry();
        mSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // https scheme
        mSchemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);

        //Send InventoryAgent specific user agent
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
            return false;
        }
        HttpResponse response = null;
        try {
            response = httpclient.execute(post, context);
        } catch (ClientProtocolException e) {
            FlyveLog.log(this, "Protocol Exception Error : " + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError("Protocol exception error");
            FlyveLog.e(e.getMessage());
            return false;
        } catch (IOException e) {
            FlyveLog.log(this, "IO error : " + e.getLocalizedMessage(), Log.ERROR);
            FlyveLog.log(this, "IO error : " + url.toExternalForm(), Log.ERROR);
            callback.onTaskError("Server doesn't reply");
            FlyveLog.e(e.getMessage());
            return false;
        } catch (Exception e) {
            callback.onTaskError("Unknow exception");
            FlyveLog.e(e.getLocalizedMessage());
            return false;
        }
        if (response == null) {
            FlyveLog.log(this, "No HTTP response ", Log.ERROR);
            callback.onTaskError("Server doesn't reply");
            return false;
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

            callback.onTaskSuccess("Inventory sent");

            FlyveLog.d(sb.toString());
            return true;
        } catch (Exception e) {
            FlyveLog.e(e.getMessage());
            callback.onTaskSuccess("Inventory sent fail");
        }

        return false;
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
