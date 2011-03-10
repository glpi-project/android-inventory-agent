package org.fusioninventory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import org.fusioninventory.categories.Category;
import org.fusioninventory.categories.Telephony;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;

public class InventoryTask extends Thread {
	
	/*
	 * TODO: Implémenter l'inventaire sous forme de Hashmap/Hashtable
	 * <string,string> pour le moment
	 */

	public ArrayList<Category> content = null;
	public Date start = null, end = null;
	public Context ctx = null;
	static final int OK = 0;
	static final int NOK = 1;

	public Boolean running = false;
	public int progress = 0;
	
	public InventoryTask(Context context) {
		ctx = context;
	}

	public String toXML() {

		if (content != null) {

//			KXmlSerializer serializer = new KXmlSerializer();
			XmlSerializer serializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
						
			
			
//			serializer.setProperty(
//			   "http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "   ");
//			// also set the line separator
//			serializer.setProperty(
//			   "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
			try {
				serializer.setOutput(writer);
				serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
				// indentation as 3 spaces

				serializer.startDocument("utf-8", true);
				// Start REQUEST
				serializer.startTag(null, "REQUEST");
				// Start CONTENT
				serializer.startTag(null,"QUERY");
				serializer.text("INVENTORY");
				serializer.endTag(null,"QUERY");
				
				serializer.startTag(null,"DEVICE-ID");
				serializer.text("android");
				serializer.endTag(null,"DEVICE-ID");
				
				serializer.startTag(null, "CONTENT");
				// Start ACCESSLOG
				serializer.startTag(null, "ACCESSLOG");
				
				serializer.startTag(null, "LOGDATE");

				serializer.text(DateFormat.format("yyyy-mm-dd hh:MM:ss", start)
						.toString());
				serializer.endTag(null, "LOGDATE");

				serializer.startTag(null, "USERID");
				serializer.text("N/A");
				serializer.endTag(null, "USERID");

				serializer.endTag(null, "ACCESSLOG");
				// End ACCESSLOG
								
				for (Category cat : this.content) {
					
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

	@Override
	public synchronized void run() {
		running = true;
		start = new Date();
		
		FusionInventory.log(this, "adding telephony", Log.INFO);
		content = new ArrayList<Category>();
		content.add(new Telephony(ctx));
//		FusionInventory.log(this, "waiting 5 secs", Log.INFO);
//		SystemClock.sleep(5000);
		FusionInventory.log(this, "end of inventory", Log.INFO);
		end = new Date();
		running = false;
	}
	
//	@Override
//	protected  run () {
//		// TODO Auto-generated method stub
//		start = new Date();
//
//		content = new ArrayList<Category>();
//		content.add(new Telephony(ctx));
//		SystemClock.sleep(5000);
//
//		end = new Date();
//		return this.toXML();
//	}


	
}
