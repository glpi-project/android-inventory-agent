package org.fusioninventory.categories;

import java.util.Hashtable;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;

public class Category {

	public Hashtable<String, Object> content;
	public Context ctx;
	public String type;

	public Category(Context ctx) {
		this.ctx = ctx;
		this.content = new Hashtable<String, Object>();
	}

	
	
	public void toXML(XmlSerializer serializer) {
		try {
			serializer.startTag(null, this.type);

			for (String prop : this.content.keySet()) {

				serializer.startTag(null, prop);
				serializer.text(this.content.get(prop).toString());
				serializer.endTag(null, prop);
			}
			
			serializer.endTag(null,this.type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
