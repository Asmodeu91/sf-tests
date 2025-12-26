package com.automation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "searchInput")
    private WebElement searchInput;

    @FindBy(id = "searchButton")
    private WebElement searchButton;

    @FindBy(id = "pt-login")
    private WebElement loginLink;

    @FindBy(id = "n-mainpage-description")
    private WebElement mainPageLink;

    @FindBy(css = "#p-logo a.mw-wiki-logo, #p-navigation .mw-wiki-logo")
    private WebElement wikiLogo;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void enterSearchText(String text) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(text);
    }

    public void clickSearchButton() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void searchFor(String text) {
        enterSearchText(text);
        clickSearchButton();
    }

    public boolean isMainPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(wikiLogo));
            return wikiLogo.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void goToMainPage() {
        try {
            mainPageLink.click();
        } catch (StaleElementReferenceException e) {
            // Перепоиск элемента
            mainPageLink = driver.findElement(By.id("n-mainpage-description"));
            mainPageLink.click();
        }
    }

    public WebElement getMainPageLinkElement() {
        return mainPageLink;
    }

    public void clickLoginLink() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
        } catch (Exception e) {
            WebElement altLoginLink = driver.findElement(By.cssSelector("#pt-login a"));
            altLoginLink.click();
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public void refreshPageElements() {
        PageFactory.initElements(driver, this);
    }

    public void switchToEnglish() {
        try {
            WebElement englishLink = driver.findElement(By.partialLinkText("English"));
            englishLink.click();
        } catch (Exception e) {
            // Попробуем другой локатор
            WebElement englishLink = driver.findElement(By.cssSelector("a[title='English']"));
            englishLink.click();
        }
    }
}
