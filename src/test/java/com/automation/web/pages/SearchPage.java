package com.automation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;


    @FindBy(css = "h1.firstHeading")
    private WebElement pageTitle;

    @FindBy(css = "div.mw-search-result-heading a")
    private List<WebElement> searchResults;

    @FindBy(css = "ul.mw-search-results li")
    private List<WebElement> searchResultItems;

    @FindBy(css = ".searchdidyoumean")
    private WebElement didYouMeanSuggestion;

    @FindBy(css = ".mw-search-nonefound")
    private WebElement noResultsMessage;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public String getPageTitleText() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.getText();
        } catch (Exception e) {
            return driver.getTitle();
        }
    }

    public int getSearchResultsCount() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfAllElements(searchResults),
                    ExpectedConditions.visibilityOfAllElements(searchResultItems),
                    ExpectedConditions.visibilityOf(noResultsMessage)
            ));

            if (!searchResults.isEmpty()) {
                return searchResults.size();
            }
            if (!searchResultItems.isEmpty()) {
                return searchResultItems.size();
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickFirstSearchResult() {
        try {
            if (!searchResults.isEmpty()) {
                wait.until(ExpectedConditions.elementToBeClickable(searchResults.get(0))).click();
                return;
            }

            if (!searchResultItems.isEmpty()) {
                WebElement firstItemLink = searchResultItems
                        .get(0)
                        .findElement(By.cssSelector("a"));
                wait.until(ExpectedConditions.elementToBeClickable(firstItemLink)).click();
            }
        } catch (Exception e) {
            System.out.println("Не удалось кликнуть на первый результат: " + e.getMessage());
        }
    }

    public boolean isDidYouMeanDisplayed() {
        try {
            return didYouMeanSuggestion.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNoResultsFound() {
        try {
            return noResultsMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSearchResultsPage() {
        try {
            String title = getPageTitleText().toLowerCase();
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            return title.contains("поиск") || title.contains("search") ||
                    currentUrl.contains("search") || currentUrl.contains("w/index.php");
        } catch (Exception e) {
            return false;
        }
    }

    public void refreshPageElements() {
        PageFactory.initElements(driver, this);
    }
}
