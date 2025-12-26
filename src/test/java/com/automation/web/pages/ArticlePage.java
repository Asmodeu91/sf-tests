package com.automation.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ArticlePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "firstHeading")
    private WebElement articleHeading;

    @FindBy(id = "mw-content-text")
    private WebElement contentBlock;

    @FindBy(id = "toc")
    private WebElement tocBlock;

    @FindBy(id = "siteSub")
    private WebElement subtitle;

    public ArticlePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public String getHeadingText() {
        wait.until(ExpectedConditions.visibilityOf(articleHeading));
        return articleHeading.getText();
    }

    public boolean isContentDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(contentBlock));
            return contentBlock.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTocDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(tocBlock));
            return tocBlock.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubtitleText() {
        try {
            wait.until(ExpectedConditions.visibilityOf(subtitle));
            return subtitle.getText();
        } catch (Exception e) {
            return "";
        }
    }


    public boolean pageContainsText(String text) {
        wait.until(ExpectedConditions.visibilityOf(contentBlock));
        return contentBlock.getText().contains(text);
    }

    public void refreshPageElements() {
        PageFactory.initElements(driver, this);
    }

    public static By headingLocator() {
        return By.id("firstHeading");
    }
}
