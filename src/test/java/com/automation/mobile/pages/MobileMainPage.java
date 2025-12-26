package com.automation.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;

import java.time.Duration;

public class MobileMainPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public MobileMainPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isMainPageDisplayed() {
        try {
            Thread.sleep(5000);

            System.out.println("Получаю pageSource...");
            System.out.println(driver.getPageSource());

            WebElement searchContainer = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            AppiumBy.id("org.wikipedia:id/search_container")
                    )
            );
            return searchContainer.isDisplayed();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void searchFor(String query) {
        WebElement searchContainer = wait.until(
                ExpectedConditions.elementToBeClickable(
                        AppiumBy.id("org.wikipedia:id/search_container")
                )
        );
        searchContainer.click();

        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        AppiumBy.id("org.wikipedia:id/search_src_text")
                )
        );

        searchInput.clear();
        searchInput.sendKeys(query);
    }
}
