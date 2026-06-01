/**
 * ---------------------------------------------------------------------
 * GLPI Android Inventory Agent
 * Copyright (C) 2019 Teclib.
 *
 * https://glpi-project.org
 *
 * Based on Flyve MDM Inventory Agent For Android
 * Copyright © 2018 Teclib. All rights reserved.
 *
 * ---------------------------------------------------------------------
 *
 *  LICENSE
 *
 *  This file is part of GLPI Android Inventory Agent.
 *
 *  GLPI Android Inventory Agent is a subproject of GLPI.
 *
 *  GLPI Android Inventory Agent is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  GLPI Android Inventory Agent is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @copyright Copyright © 2019 Teclib. All rights reserved.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/glpi-project/android-inventory-agent
 *  @link      https://glpi-project.org/glpi-network/
 *  ---------------------------------------------------------------------
 */

package org.glpi.inventory.agent.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.glpi.inventory.agent.R;
import org.glpi.inventory.agent.schema.ServerSchema;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;

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
            serverSchema.setItemtype(jo.getString("itemtype"));
            serverSchema.setSerial(jo.getString("serial"));
            serverSchema.setName(jo.optString("name", ""));
            serverSchema.setClientID(jo.optString("client_id", ""));
            serverSchema.setClientSecret(jo.optString("client_secret", ""));
            serverSchema.setOauthToken(jo.optString("oauth_token", ""));
            serverSchema.setRefreshToken(jo.optString("refresh_token", ""));
        } catch (JSONException e) {
            AgentLog.e(e.getMessage());
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
            AgentLog.log(this, "No XML Inventory ", Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_inventory));
            return;
        }

        try {
            url = new URL(serverSchema.getAddress());
            AgentLog.d(url.toString());
        } catch (MalformedURLException e) {
            AgentLog.log(this, appContext.getResources().getString(R.string.error_url_is_malformed) + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_malformed));
            return;
        }

        if (url == null || "".equals(serverSchema.getAddress())) {
            AgentLog.log(this, appContext.getResources().getString(R.string.error_url_is_not_found), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_url_is_not_found));
            return;
        }

        if (this.serverSchema == null) {
            AgentLog.log(this, appContext.getResources().getString(R.string.error_send_fail), Log.ERROR);
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
            String clientId = serverSchema.getClientID();
            String clientSecret = serverSchema.getClientSecret();
            String tokenUrl = "https://stanislaskita.fr35.glpi-network.cloud/api.php/token"; // TODO: replace with dynamic URL
            String accessToken = null;

            if (clientId != null && !clientId.isEmpty()) {
                AgentLog.d("OAuth - client_id present, starting OAuth flow. tokenUrl=" + tokenUrl);
                String cachedToken = serverSchema.getOauthToken();
                if (cachedToken != null && !cachedToken.isEmpty()) {
                    AgentLog.d("OAuth - using cached access token");
                    accessToken = cachedToken;
                } else {
                    AgentLog.d("OAuth - no cached token, requesting new access token");
                    accessToken = getOAuthToken(clientId, clientSecret, tokenUrl);
                    serverSchema.setOauthToken(accessToken);
                    persistTokens(accessToken, serverSchema.getRefreshToken());
                }
            } else {
                AgentLog.d("OAuth - no client_id configured, skipping OAuth");
            }

            DataLoader dl = new DataLoader();

            AgentLog.d("OAuth - sending inventory to " + serverSchema.getAddress() + " (token=" + (accessToken != null ? "present" : "absent") + ")");
            HttpResponse response = dl.secureLoadData(appContext, serverSchema, lastXMLResult, accessToken);
            int statusCode = response.getStatusLine().getStatusCode();
            AgentLog.d("OAuth - server response status: " + statusCode);

            if (statusCode == 401 && clientId != null && !clientId.isEmpty()) {
                AgentLog.d("OAuth - access token rejected (401), token may be expired or client revoked");
                String refreshToken = serverSchema.getRefreshToken();

                if (refreshToken != null && !refreshToken.isEmpty()) {
                    try {
                        accessToken = refreshAccessToken(refreshToken, tokenUrl);
                        persistTokens(accessToken, serverSchema.getRefreshToken());
                        AgentLog.d("OAuth - token refreshed, retrying inventory");
                        response = dl.secureLoadData(appContext, serverSchema, lastXMLResult, accessToken);
                        AgentLog.d("OAuth - retry response status: " + response.getStatusLine().getStatusCode());
                    } catch (Exception e) {
                        AgentLog.e("OAuth - token refresh failed: " + e.getMessage());
                        serverSchema.setOauthToken("");
                        serverSchema.setRefreshToken("");
                        persistTokens("", "");
                        callback.onTaskError(appContext.getResources().getString(R.string.error_refresh_token));
                        return;
                    }
                } else {
                    AgentLog.d("OAuth - no refresh token, attempting full re-authentication");
                    serverSchema.setOauthToken("");
                    persistTokens("", "");
                    try {
                        accessToken = getOAuthToken(clientId, clientSecret, tokenUrl);
                        serverSchema.setOauthToken(accessToken);
                        persistTokens(accessToken, serverSchema.getRefreshToken());
                        AgentLog.d("OAuth - re-authentication successful, retrying inventory");
                        response = dl.secureLoadData(appContext, serverSchema, lastXMLResult, accessToken);
                        AgentLog.d("OAuth - retry response status: " + response.getStatusLine().getStatusCode());
                    } catch (Exception e) {
                        AgentLog.e("OAuth - re-authentication failed: " + e.getMessage());
                        callback.onTaskError(appContext.getResources().getString(R.string.error_oauth_unauthorized));
                        return;
                    }
                }
            }

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
                AgentLog.log(this, line, Log.VERBOSE);
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

            AgentLog.d(sb.toString());

        } catch (ClientProtocolException e) {
            AgentLog.log(this, appContext.getResources().getString(R.string.error_protocol) + e.getLocalizedMessage(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_protocol));
            AgentLog.e(e.getMessage());
        } catch (IOException e) {
            AgentLog.log(this, "IO error : " + e.getLocalizedMessage(), Log.ERROR);
            AgentLog.log(this, "IO error : " + url.toExternalForm(), Log.ERROR);
            callback.onTaskError(appContext.getResources().getString(R.string.error_server_not_response));
            AgentLog.e(e.getMessage());
        } catch (JSONException e) {
            callback.onTaskError("OAuth JSON Parsing Error: " + e.getLocalizedMessage());
            AgentLog.e(e.getLocalizedMessage());
        } catch (Exception e) {
            callback.onTaskError(appContext.getResources().getString(R.string.error_send_fail));
            AgentLog.e(e.getLocalizedMessage());
        }
    }


    private void persistTokens(String accessToken, String refreshToken) {
        AgentLog.d("OAuth - persisting tokens to preferences (refreshToken=" + (refreshToken != null && !refreshToken.isEmpty() ? "present" : "absent") + ")");
        LocalPreferences preferences = new LocalPreferences(appContext);
        try {
            JSONObject jo = preferences.loadJSONObject(serverSchema.getAddress());
            jo.put("oauth_token", accessToken != null ? accessToken : "");
            if (refreshToken != null && !refreshToken.isEmpty()) {
                jo.put("refresh_token", refreshToken);
            }
            preferences.saveJSONObject(serverSchema.getAddress(), jo);
            AgentLog.d("OAuth - tokens persisted successfully");
        } catch (JSONException e) {
            AgentLog.e("OAuth - failed to persist tokens: " + e.getMessage());
        }
    }

    private String getOAuthToken(String clientId, String clientSecret, String tokenUrl) throws Exception {
        AgentLog.d("OAuth - requesting access token from " + tokenUrl);
        URL url = new URL(tokenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn instanceof javax.net.ssl.HttpsURLConnection) {
            AgentLog.d("OAuth - configuring SSL for token request");
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new javax.net.ssl.TrustManager[]{new CustomX509TrustManager()}, new java.security.SecureRandom());

            javax.net.ssl.HttpsURLConnection sslConn = (javax.net.ssl.HttpsURLConnection) conn;
            sslConn.setSSLSocketFactory(ctx.getSocketFactory());
            sslConn.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                @Override
                public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                    return true;
                }
            });
        }

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json");

        String basicAuth = "Basic " + android.util.Base64.encodeToString(
                (clientId + ":" + clientSecret).getBytes("UTF-8"), android.util.Base64.NO_WRAP);
        conn.setRequestProperty("Authorization", basicAuth);

        String data = "grant_type=client_credentials" +
                "&client_id=" + java.net.URLEncoder.encode(clientId, "UTF-8") +
                "&client_secret=" + java.net.URLEncoder.encode(clientSecret, "UTF-8") +
                "&scope=inventory";
        AgentLog.d("OAuth - request body: " + data.replaceAll("client_secret=[^&]*", "client_secret=***"));

        java.io.OutputStream os = conn.getOutputStream();
        os.write(data.getBytes("UTF-8"));
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String accessToken = jsonResponse.getString("access_token");

            if (jsonResponse.has("refresh_token")) {
                AgentLog.d("OAuth - refresh token received and stored");
                serverSchema.setRefreshToken(jsonResponse.getString("refresh_token"));
            } else {
                AgentLog.d("OAuth - no refresh token in response");
            }
            AgentLog.d("OAuth - access token obtained successfully");
            return accessToken;

        } else {
            InputStream es = conn.getErrorStream();
            if (es != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(es));
                AgentLog.e("OAuth - token request error body: " + br.readLine());
            }
            throw new IOException("OAuth - HTTP " + responseCode + " : " + conn.getResponseMessage());
        }
    }

    private String refreshAccessToken(String refreshToken, String tokenUrl) throws Exception {
        AgentLog.d("OAuth - requesting token refresh from " + tokenUrl);
        URL url = new URL(tokenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn instanceof javax.net.ssl.HttpsURLConnection) {
            AgentLog.d("OAuth - configuring SSL for refresh request");
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new javax.net.ssl.TrustManager[]{new CustomX509TrustManager()}, new java.security.SecureRandom());

            javax.net.ssl.HttpsURLConnection sslConn = (javax.net.ssl.HttpsURLConnection) conn;
            sslConn.setSSLSocketFactory(ctx.getSocketFactory());
            sslConn.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                @Override
                public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                    return true;
                }
            });
        }

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json");

        String basicAuth = "Basic " + android.util.Base64.encodeToString(
                (serverSchema.getClientID() + ":" + serverSchema.getClientSecret()).getBytes("UTF-8"), android.util.Base64.NO_WRAP);
        conn.setRequestProperty("Authorization", basicAuth);
        AgentLog.d("OAuth - Authorization: Basic header set for refresh request");

        String data = "grant_type=refresh_token" +
                "&refresh_token=" + java.net.URLEncoder.encode(refreshToken, "UTF-8") +
                "&client_id=" + java.net.URLEncoder.encode(serverSchema.getClientID(), "UTF-8") +
                "&client_secret=" + java.net.URLEncoder.encode(serverSchema.getClientSecret(), "UTF-8");

        java.io.OutputStream os = conn.getOutputStream();
        os.write(data.getBytes("UTF-8"));
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        AgentLog.d("OAuth - refresh endpoint response code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String newAccessToken = jsonResponse.getString("access_token");

            serverSchema.setOauthToken(newAccessToken);
            if (jsonResponse.has("refresh_token")) {
                AgentLog.d("OAuth - new refresh token received");
                serverSchema.setRefreshToken(jsonResponse.getString("refresh_token"));
            }

            AgentLog.d("OAuth - token refresh successful");
            return newAccessToken;

        } else {
            InputStream es = conn.getErrorStream();
            if (es != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(es));
                AgentLog.e("OAuth - refresh error body: " + br.readLine());
            }
            throw new IOException("OAuth - HTTP " + responseCode + " : Unable to refresh OAuth token");
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
