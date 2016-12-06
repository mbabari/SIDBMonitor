package com.support.SIDBMonitor;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class propertyHandler {
	 private String propertyName = "";
	    private Properties prop = new Properties();

	    public propertyHandler(String propertyName){
	        this.propertyName=propertyName;
	        try { prop.load(new FileInputStream(propertyName)); } catch (IOException e) { System.out.println(" Property loading error " +
	             e.getMessage()); }
	    }
	    public String getProperty(String name)
	    {
	        return prop.getProperty(name);
	    }
	    public void setProperty(String name, String value)
	    {
	        prop.setProperty(name, value);
	       
	        try { prop.store(new FileOutputStream(propertyName), null); } catch (IOException e) { System.out.println("Property setting error" +
	           e.getMessage()); }
	    }

}
