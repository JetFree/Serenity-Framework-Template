package com.project.qa.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Evgeny.Gurinovich on 29.04.2016.
 */
public class PropertiesLoader {
    private Properties properties = new Properties();

    public PropertiesLoader() {
        try {
            this.properties.load(new FileInputStream("serenity.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PropertiesLoader(String fileProperties) {
        try {
            this.properties.load(new FileInputStream(fileProperties));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
}
