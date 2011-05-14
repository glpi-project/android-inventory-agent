package org.fusioninventory;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

import org.fusioninventory.categories.Categories;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;

public class InventoryTask {

    /*
     * TODO: Implémenter l'inventaire sous forme de Hashmap/Hashtable
     * <string,string> pour le moment
     */

    public ArrayList<Categories> mContent = null;
    public Date mStart = null, mEnd = null;
    public Context ctx = null;
    static final int OK = 0;
    static final int NOK = 1;

    public Boolean running = false;
    public int progress = 0;

    private Agent mAgent;
    private FusionInventoryApp mFusionApp;
    
    public InventoryTask(Agent agent) {
        mAgent= agent;
        ctx = mAgent.getApplicationContext();
        mFusionApp = (FusionInventoryApp) mAgent.getApplication();
        FusionInventory.log(this, "FusionInventoryApp = " + mFusionApp.toString(), Log.VERBOSE);
    }

    public String toXML() {

        if (mContent != null) {

            // KXmlSerializer serializer = new KXmlSerializer();
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            // serializer.setProperty(
            // "http://xmlpull.org/v1/doc/properties.html#serializer-indentation",
            // "   ");
            // // also set the line separator
            // serializer.setProperty(
            // "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator",
            // "\n");
            try {
                serializer.setOutput(writer);
                serializer
                        .setFeature(
                                "http://xmlpull.org/v1/doc/features.html#indent-output",
                                true);
                // indentation as 3 spaces

                serializer.startDocument("utf-8", true);
                // Start REQUEST
                serializer.startTag(null, "REQUEST");
                // Start CONTENT
                serializer.startTag(null, "QUERY");
                serializer.text("INVENTORY");
                serializer.endTag(null, "QUERY");

                serializer.startTag(null, "DEVICEID");
                serializer.text(mFusionApp.getDeviceID());
                serializer.endTag(null, "DEVICEID");

                serializer.startTag(null, "CONTENT");
                // Start ACCESSLOG
                serializer.startTag(null, "ACCESSLOG");

                serializer.startTag(null, "LOGDATE");

                serializer.text(DateFormat.format("yyyy-mm-dd hh:MM:ss", mStart)
                        .toString());
                serializer.endTag(null, "LOGDATE");

                serializer.startTag(null, "USERID");
                serializer.text("N/A");
                serializer.endTag(null, "USERID");

                serializer.endTag(null, "ACCESSLOG");
                // End ACCESSLOG

                for (Categories cat : mContent) {

                    cat.toXML(serializer);
                }

                serializer.endTag(null, "CONTENT");
                serializer.endTag(null, "REQUEST");
                serializer.endDocument();
                return (writer.toString());
            } catch (Exception e) {
                // TODO: handle exception
                throw new RuntimeException(e);
            }

        }
        return null;
    }

    
    @SuppressWarnings("unchecked")
    public synchronized void run() {
        
        running = true;
        mStart = new Date();

        mContent = new ArrayList<Categories>();
        
        String [] categories = { 
                "Bios",
                "Hardware",
                "Simcards",
                "Cpus",
                "LocationProviders",
                "Videos",
                "Cameras",
                "BluetoothAdapterCategory", // <- there is already a BluetoothAdapter class in android SDK
                "Networks",
                "Softwares"
        };
        
        Class<Categories> cat_class;
        
        for(String c : categories) {
            cat_class = null;
            FusionInventory.log(this, String.format("INVENTORY of %s", c),Log.VERBOSE);
            try {
                cat_class = (Class <Categories>) Class.forName(String.format("org.fusioninventory.categories.%s",c));
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(cat_class!=null) {
                try {
                    Constructor<Categories> co = cat_class.getConstructor(Context.class);
                    mContent.add(co.newInstance(mFusionApp));
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
        }
        
//        this.addBios();
//        this.addHardware();
//        this.addSimcards();
        
        //TODO this must goes into a monitoring task
        //this.addPhoneStatus();
        
        // FusionInventory.log(this, "waiting 5 secs", Log.INFO);
        //SystemClock.sleep(1000);
        FusionInventory.log(this, "end of inventory", Log.INFO);
        mEnd = new Date();
        running = false;
    }

//    public void addBios() {
//
//        content.add(new Bios(mFusionApp));
//    }
//
//    public void addSimcards() {
//        // TODO Reorganize informations from TelephonyManager
//        content.add(new Simcards(mFusionApp));
//    }
//
//    
//    public void addHardware() {
//        content.add(new Hardware(mFusionApp));
//    }
//    
//    public void addProcesses() {
//        ActivityManager  activityManager = (ActivityManager) mFusionApp.getSystemService(Service.ACTIVITY_SERVICE);
//        
//        List<RunningAppProcessInfo> ps = activityManager.getRunningAppProcesses();
////        for(RunningAppProcessInfo process : ps) {
////            content.add(new Processes(mFusionApp,process));
////        }
//        
//    }
//    
//    public void addPhoneStatus() {
//        content.add(new PhoneStatus(mFusionApp));
//    }
//    // @Override
//    // protected run () {
//    // // TODO Auto-generated method stub
//    // start = new Date();
//    //
//    // content = new ArrayList<Category>();
//    // content.add(new Telephony(ctx));
//    // SystemClock.sleep(5000);
//    //
//    // end = new Date();
//    // return this.toXML();
//    // }

}
