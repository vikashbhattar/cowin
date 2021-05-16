package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CowinResources {

	public static Properties cowinProperties;

	static {
		cowinProperties = new Properties();
		try {
			ClassLoader cl = CowinResources.class.getClassLoader();
			InputStream stream = cl.getResourceAsStream("cowin.properties");
			cowinProperties.load(stream);
		} catch (IOException e) {
			System.out.println("Exception while loading cowin properties file. " + e.getMessage());
		}
	}

	public static Properties getCowinProperties() {
		return cowinProperties;
	}

	public static String getProperty(String key) {
		return cowinProperties.get(key).toString();
	}
	
	public static int getIntProperty(String key) {
		return Integer.parseInt(cowinProperties.getProperty(key));
	}
}
