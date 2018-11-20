package org.flyve.inventory.agent.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class UtilsAgent {

    /**
     * @param fileName name of the xml to extract
     * @param context
     * @return string xml
     */
    public static String getXml(String fileName, Context context) {
        String xmlString = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            xmlString = new String(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return xmlString;
    }

}
