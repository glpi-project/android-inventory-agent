package org.fusioninventory;

import java.io.StringWriter;
import java.io.Writer;

import android.os.AsyncTask;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Date;

import org.fusioninventory.categories.Category;
import org.fusioninventory.categories.Telephony;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;

public class InventoryTask extends AsyncTask<Void, Integer, String>{
	
	/*
	 * TODO: Implémenter l'inventaire sous forme de Hashmap/Hashtable
	 * <string,string> pour le moment
	 */

	public ArrayList<Category> content = null;
	public Date start = null, end = null;
	public Context ctx = null;
	static final int OK = 0;
	static final int NOK = 1;

	public InventoryTask(Context context) {
		ctx = context;
	}

	public String toXML() {

		if (content != null) {
			XmlSerializer serializer = Xml.newSerializer();

			StringWriter writer = new StringWriter();
			
			try {
				serializer.setOutput(writer);
				serializer.startDocument("utf-8", null);
				// Start REQUEST
				serializer.startTag(null, "REQUEST");
				// Start CONTENT
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
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		start = new Date();

		content = new ArrayList<Category>();
		content.add(new Telephony(ctx));

		end = new Date();
		return this.toXML();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		
	}
}
