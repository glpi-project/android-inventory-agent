package org.fusioninventory.categories;

import java.util.Properties;

import android.content.Context;

public class Jvm
        extends Categories {

    /**
     * 
     */
    private static final long serialVersionUID = 3528873342443549732L;

    public Jvm(Context xCtx) {
        super(xCtx);
        // TODO Auto-generated constructor stub

        Category c = new Category(mCtx,"JVMS");
        Properties props = System.getProperties();
        
        //for(Object prop: props.keySet() ) {
        //    FusionInventory.log(this, String.format("PROP %s = %s" , (String)prop, props.get(prop) ) , Log.VERBOSE);
        //}

        c.put("NAME", (String)props.getProperty("java.vm.name"));
        c.put("VENDOR", (String)props.getProperty("java.vm.vendor"));
        c.put("HOME", (String)props.getProperty("java.home"));
        c.put("VERSION", (String)props.getProperty("java.vm.version"));
        c.put("CLASSPATH", (String)props.getProperty("java.class.path"));
        this.add(c);
    }
}
