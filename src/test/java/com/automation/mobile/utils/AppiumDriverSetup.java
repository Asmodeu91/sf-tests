package com.automation.mobile.utils;

import com.automation.config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class AppiumDriverSetup {

    protected AndroidDriver driver;

    @BeforeClass
    public void setUp() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(TestConfig.getPlatformName())
                .setPlatformVersion(TestConfig.getPlatformVersion())
                .setDeviceName(TestConfig.getDeviceName())
                .setAutomationName(TestConfig.getAutomationName())
                .setAppPackage(TestConfig.getAppPackage())
                .setAppActivity(TestConfig.getAppActivity())
                .setNoReset(true)
                .setAutoGrantPermissions(true);

        String appPath = TestConfig.getAppPath();
        if (appPath != null && !appPath.isEmpty()) {
            options.setApp(appPath);
        }

        try {
            URL serverUrl = new URL(TestConfig.getAppiumUrl());
            driver = new AndroidDriver(serverUrl, options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Некорректный URL Appium сервера", e);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
