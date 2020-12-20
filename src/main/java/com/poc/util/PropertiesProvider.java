package com.poc.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class PropertiesProvider {

	private static Map<String, String> loadedProperties = new HashMap<String, String>();

	static {
		Yaml yaml = new Yaml();
		try {
			InputStream yamlPropertiesFileInputStream = new FileInputStream("src/main/resources/poc_properties.yaml");
			loadedProperties = yaml.load(yamlPropertiesFileInputStream);
			System.out.println("Properties loaded from YAML property file :==> "+ loadedProperties);
		} catch (FileNotFoundException e) {
			System.out.println("!!!! Missing src\\main\\resources\\poc_properties.yaml file !!!!");
			e.printStackTrace();
		}
	}

	public static String getValueFor(String key) {
		return loadedProperties.get(key); 
	}
}
