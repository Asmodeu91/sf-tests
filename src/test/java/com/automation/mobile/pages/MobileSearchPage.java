package com.automation.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class MobileSearchPage {

    private final AndroidDriver driver;

    public MobileSearchPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public int getSearchResultsCount() {
        List<WebElement> results = driver.findElements(
                AppiumBy.id("org.wikipedia:id/page_list_item_title")
        );
        return results.size();
    }

    public String getFirstResultTitle() {
        List<WebElement> results = driver.findElements(
                AppiumBy.id("org.wikipedia:id/page_list_item_title")
        );
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0).getText();
    }

    public void clickFirstSearchResult() {
        List<WebElement> results = driver.findElements(
                AppiumBy.id("org.wikipedia:id/page_list_item_title")
        );
        if (!results.isEmpty()) {
            results.get(0).click();
        }
    }

    public boolean isSearchEmpty() {
        List<WebElement> emptyView = driver.findElements(
                AppiumBy.id("org.wikipedia:id/search_empty_view")
        );
        return !emptyView.isEmpty();
    }
}
