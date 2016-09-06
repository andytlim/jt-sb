package com.jt.sb.conf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties {
	
	protected static Logger log = LoggerFactory.getLogger(ApplicationProperties.class);
	private static Properties prop = new Properties();
	private static Set<String> sdlcKeywords = new HashSet<String>();
	private static Set<String> osKeywords = new HashSet<String>();

	static {
        try { 
        	initialize();
        	loadAdditionalProperties();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
	
	public static void initialize() throws IOException {
		BufferedReader br = null;		
		br = new BufferedReader(new InputStreamReader(ApplicationProperties.class.getClassLoader()
                .getResourceAsStream("application.properties")));			
		prop.load(br);		
		br.close();
		
		sdlcKeywords = new Gson().fromJson(prop.getProperty("sdlc.namespaces"), new TypeToken<Set<String>>(){}.getType());
		osKeywords = new Gson().fromJson(prop.getProperty("os.namespaces"), new TypeToken<Set<String>>(){}.getType());		
	}
	
	public static void loadAdditionalProperties() throws IOException {
		List<String> propertyFiles = new Gson().fromJson(prop.getProperty("project.properties"), new TypeToken<List<String>>(){}.getType());
		
		if (propertyFiles == null) return;
		
		for (int i = 0; i < propertyFiles.size(); i++) {
			String propertyFile = propertyFiles.get(i);
			BufferedReader br = null;
			Properties applicationProperties = new Properties();
			if (System.getProperty("os.name").contains("Windows")) {
				log.info("Loading configuration from config/" + propertyFile + "...");
				br = new BufferedReader(new InputStreamReader(new FileInputStream("config/" + propertyFile)));
			}
			else {
				log.info("Loading configuration from " + prop.getProperty("project.root") + "/config/" + propertyFile + "...");
				br = new BufferedReader(new InputStreamReader(new FileInputStream(prop.getProperty("project.root") + "/config/" + propertyFile)));
			}
			applicationProperties.load(br);
			prop.putAll(applicationProperties);
			br.close();
		}
	}
	
	public static String get(String key) {
		String[] namespace = key.split("\\.");
		StringBuilder newKey = new StringBuilder();
		
		if (sdlcKeywords.contains(namespace[0])) {
			newKey.append(namespace[0]).append(".");
			newKey.append(prop.getProperty("env"));
			for (int i = 1; i < namespace.length; i++) {
				newKey.append(".").append(namespace[i]);
			}
		}
		else if (System.getProperty("os.name").contains("Windows") && osKeywords.contains(namespace[0])) {
			newKey.append(namespace[0]).append(".");
			newKey.append("win");
			for (int i = 1; i < namespace.length; i++) {
				newKey.append(".").append(namespace[i]);
			}
		}
		else
			newKey.append(key);		
		
		return prop.getProperty(newKey.toString());
	}
}
