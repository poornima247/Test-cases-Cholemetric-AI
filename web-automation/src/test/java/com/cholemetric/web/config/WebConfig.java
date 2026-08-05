package com.cholemetric.web.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class WebConfig {
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(new FileInputStream("config/web-config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        return System.getProperty("BASE_URL");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("HEADLESS", properties.getProperty("headless")));
    }
}
