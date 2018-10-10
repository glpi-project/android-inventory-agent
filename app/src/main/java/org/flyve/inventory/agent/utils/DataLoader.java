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
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.flyve.inventory.agent.ui.InventoryAgentApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class DataLoader {

    public HttpResponse secureLoadData(Context appContext, InventoryAgentApp mFusionApp, String lastXML) throws
            IOException, NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{new CustomX509TrustManager()}, new SecureRandom());

        HttpClient client = new DefaultHttpClient();

        SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = client.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        sr.register(new Scheme("https", ssf, 443));

        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);

        EnvironmentInfo enviromentInfo = new EnvironmentInfo(appContext);
        String version = "v0.0.0";
        if(enviromentInfo.getIsLoaded()) {
            version = "v" + enviromentInfo.getVersion();
        }
        HttpProtocolParams.setUserAgent(params, "Inventory-Agent-Android_" + version);

        DefaultHttpClient sslClient = new DefaultHttpClient(ccm, client.getParams());

        HttpPost post;
        post = new HttpPost(mFusionApp.getUrl());
        sslClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) {
                for (Header h : request.getAllHeaders()) {
                    FlyveLog.log(this, "HEADER : " + h.getName() + "=" + h.getValue(), Log.VERBOSE);
                }
            }
        });

        try {
            post.setEntity(new StringEntity(lastXML));
        } catch (UnsupportedEncodingException e1) {
            FlyveLog.e(e1.getMessage());
        }

        HttpContext context = new BasicHttpContext();
        URL url = new URL(mFusionApp.getUrl());

        String login = mFusionApp.getCredentialsLogin();
        if (!login.equals("")) {
            FlyveLog.log(this, "HTTP credentials given : use it if necessary", Log.VERBOSE);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(url.getHost(), AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(mFusionApp.getCredentialsLogin(),
                            mFusionApp.getCredentialsPassword()));
            context.setAttribute("http.auth.credentials-provider", credentialsProvider);
        }

        return sslClient.execute(post, context);

    }

}