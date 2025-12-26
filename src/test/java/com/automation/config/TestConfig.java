package com.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream is = TestConfig.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (is == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            PROPERTIES.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить config.properties", e);
        }
    }

    private static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Свойство не найдено: " + key);
        }
        return value;
    }

    /* ---------- Web ---------- */

    public static String getWebBaseUrl() {
        return getProperty("web.base.url");
    }

    public static int getWebTimeoutSeconds() {
        return Integer.parseInt(getProperty("web.timeout.seconds"));
    }

    /* ---------- Mobile ---------- */

    public static String getDeviceName() {
        return getProperty("mobile.device.name");
    }

    public static String getAppiumUrl() {
        return getProperty("mobile.appium.server.url");
    }

    public static String getPlatformName() {
        return getProperty("mobile.platform.name");
    }

    public static String getPlatformVersion() {
        return "16"; // под твой эмулятор
    }
    public static String getAutomationName() {
        return getProperty("mobile.automation.name");
    }

    public static String getAppPath() {
        return getProperty("mobile.app.path");
    }

    public static String getAppPackage() {
        return getProperty("mobile.app.package");
    }

    public static String getAppActivity() {
        return getProperty("mobile.app.activity");
    }
}
