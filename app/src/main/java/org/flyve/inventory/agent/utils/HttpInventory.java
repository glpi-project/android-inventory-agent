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
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.schema.ServerSchema;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpInventory {

    private Context appContext;
    private URL url = null;

    private static Handler uiHandler;

    static {
        uiHandler = new Handler(Looper.getMainLooper());
    }

    private ServerSchema serverSchema;

    private static void runOnUI(Runnable runnable) {
        uiHandler.post(runnable);
    }

    /**
     * Constructor of the class, calls the context of the current class
     * @param Context the context of the class
     */
    public HttpInventory(Context context) {
        this.appContext = context;
    }

    public ServerSchema setServerModel(String serverName) {
        LocalPreferences preferences = new LocalPreferences(appContext);
        ServerSchema serverSchema = new ServerSchema();
        try {
            JSONObject jo = preferences.loadJSONObject(serverName);
            serverSchema.setAddress(jo.getString("address"));
            serverSchema.setTag(jo.getString("tag"));
            serverSchema.setLogin(jo.getString("login"));
            serverSchema.setPass(jo.getString("pass"));
        } catch (JSONException e) {
            FlyveLog.e(e.getMessage());
        }
        return serverSchema;
    }

    /**
     * Sends the inventory
     * @param lastXMLResult lastXMLResult the inventory information
     * @param callback
     * @return boolean true if succeed, false otherwise
     */
    public void sendInventory(final String lastXMLResult, ServerSchema serverSchema, final OnTaskCompleted callback) {
        this.serverSchema = serverSchema;

        if (lastXMLResult == null) {
            FlyveLog.log(this, "No XML Inventory ", Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_inventory));
            return;
        }

        try {
            url = new URL(serverSchema.getAddress());
            FlyveLog.d(url.toString());
        } catch (MalformedURLException e) {
            FlyveLog.log(this, appContext.getResources().getString(R.string.error_url_is_malformed) + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_malformed));
            return;
        }

        if (url == null || "".equals(serverSchema.getAddress())) {
            FlyveLog.log(this, appContext.getResources().getString(R.string.error_url_is_not_found), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_not_found));
            return;
        }

        if (this.serverSchema == null) {
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
            HttpResponse response = dl.secureLoadData(appContext, serverSchema, lastXMLResult);

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
