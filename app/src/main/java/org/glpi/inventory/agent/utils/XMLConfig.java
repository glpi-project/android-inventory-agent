package org.glpi.inventory.agent.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Xml;

import org.glpi.inventory.agent.BuildConfig;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XMLConfig {

    public static void importServer(Context context, InputStream in) throws XmlPullParserException, IOException, JSONException {
        Map<String, String> values;
        LocalPreferences preferences = new LocalPreferences(context);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            values = readFeed(parser, preferences);
        } finally {
            in.close();
        }
        if (values != null && !values.isEmpty()) {
            Intent intent = new Intent();
            intent.setClassName("com.telelogos.mediacontact", "com.telelogos.mediacontact.com.nom.service");
            JSONObject jo = new JSONObject();
            try {
                jo.put("address", values.get("address"));
                jo.put("tag", values.get("tag"));
                jo.put("login", values.get("login"));
                jo.put("pass", values.get("pass"));
                jo.put("itemtype", values.get("itemtype"));
                jo.put("serial", values.get("serial"));
                ArrayList<String> serverArray = preferences.loadServer();
                ArrayList<String> newServerArray = new ArrayList<>();
                for (int i = 0; i < serverArray.size(); i++) {
                    if (!serverArray.get(i).equals(values.get("address"))) {
                        newServerArray.add(serverArray.get(i));
                    }
                }
                newServerArray.add(values.get("address"));
                preferences.saveServer(newServerArray);
            } catch (JSONException e) {
                AgentLog.e(e.getMessage());
                throw e;
            }
            if (preferences.loadJSONObject(values.get("address")) != null) {
                preferences.deletePreferences(values.get("address"));
            }
            preferences.saveJSONObject(values.get("address"), jo);
        }
    }

    @Nullable
    private static Map<String, String> readFeed(XmlPullParser parser, LocalPreferences preferences) throws XmlPullParserException, IOException {
        if (parser.getName().equals("map")) {
            return readMap(parser, preferences);
        }
        return null;
    }

    /**
     * read and parse a glpi preferences.xml
     *
     * @param parser      xml parser
     * @param preferences used to deleted/set SharedPreferences
     * @return empty list
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static Map<String, String> readMap(XmlPullParser parser, LocalPreferences preferences) throws XmlPullParserException, IOException {
        Map<String, String> server = new HashMap<>();
        SharedPreferences prefs = preferences.getDefaultPreferences();
        SharedPreferences.Editor prefsEditor = prefs.edit();

        try {
            int categsCount = Integer.parseInt(prefs.getString("Status_size_categories", "0"));
            for (int i = 1; i <= categsCount; i++) {
                prefsEditor.remove("Status_categories_" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int srvCount = Integer.parseInt(prefs.getString("Status_size", "0"));
            for (int i = 1; i <= srvCount; i++) {
                prefsEditor.remove("Status_" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName(); // "string" "boolean"
                int attrCount = parser.getAttributeCount();
                String name = ""; // attribute "name"
                String value = ""; // attribute "value" for tagname boolean
                for (int i = 0; i < attrCount; i++) {
                    String aName = parser.getAttributeName(i);
                    String aValue = parser.getAttributeValue(i);
                    if (aName.equals("name")) name = aValue;
                    if (aName.equals("value")) value = aValue;
                }
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String text = null;
                        if (tagname.equalsIgnoreCase("string")) {
                            eventType = parser.next();
                            if (eventType == XmlPullParser.TEXT) {
                                text = parser.getText();
                            }
                        }
                        if (text == null) text = "";
                        if (name.equalsIgnoreCase("Status_size")) {
                            //
                        } else if (name.equalsIgnoreCase("Status_0")) {
                            server.put("address", text);
                        } else if (name.equalsIgnoreCase("tag")) {
                            server.put("tag", text);
                        } else if (name.equalsIgnoreCase("login")) {
                            server.put("login", text);
                        } else if (name.equalsIgnoreCase("pass")) {
                            server.put("pass", text);
                        } else if (name.equalsIgnoreCase("itemtype")) {
                            server.put("itemtype", text);
                        } else if (name.equalsIgnoreCase("serial")) {
                            server.put("serial", text);
                        } else {
                            if (tagname.equalsIgnoreCase("string")) {
                                prefsEditor.putString(name, text);
                            } else if ((tagname.equalsIgnoreCase("boolean")) && (!value.isEmpty())) {
                                prefsEditor.putBoolean(name, value.equalsIgnoreCase("true"));
                            }
                        }
                    }
                    break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        if (server.size() > 0) {
            while (server.size() < 6) {
                server.put("empty", "");
            }
        }

        prefsEditor.apply();
        return server;
    }

    private static String readText(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, tag);
        String text = parser.nextText();
        parser.require(XmlPullParser.END_TAG, null, tag);
        return text;
    }

    public static void autoImportServer(Context context) {
        try {
            String path = getFilePath("geodis_glpi.xml");
            System.out.println("XMLConfig autoImportServer " + (path != null));
            if (path == null) return;
            importServer(context, new FileInputStream(path));
        } catch (Exception e) {
            System.out.println("XMLConfig autoImportServer exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getFilePath(String fileName) {
        File f = new File(Environment.getExternalStorageDirectory(), "Documents/" + fileName);
        return f.getPath();
    }
}
