package com.readnaturally.qa.utils;

import com.google.common.base.Function;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.steps.StepEventBus;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evgeny.Gurinovich on 25.04.2016.
 */
public class DriverUtils extends PageObject {

    private static DriverUtils utilsObj = null;
    private WebDriver driver;
    private Object monitor = new Object();

    private DriverUtils(WebDriver driver) {
        this.driver = driver;
        this.setDriver(driver);
    }

    public static DriverUtils getDriverUtils(WebDriver driver) {
        if (utilsObj == null) {
            utilsObj = new DriverUtils(driver);
        } else {
            utilsObj.driver = driver;
            utilsObj.setDriver(driver);
        }
        return utilsObj;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Random rng = new Random();

    private static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    public void waitForAjaxComplete() {
        String pageLoadStatus;
        do {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            pageLoadStatus = (String) js.executeScript("return document.readyState");
        } while (!pageLoadStatus.equals("complete"));
        logger.info("Page loaded completely!");
    }

    public void clickByJS(WebElementFacade element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public boolean isElementHiddenNow(By elementLocator) {
        Duration implicitWait = getImplicitWaitTimeout();
        WebElement element;
        synchronized (this.monitor) {
            setImplicitTimeout(0, ChronoUnit.SECONDS);
            try {
                element = getDriver().findElement(elementLocator);
            } catch (NoSuchElementException e) {
                element = null;
            }
            setImplicitTimeout((int) implicitWait.getSeconds(), ChronoUnit.SECONDS);
        }
        return element == null;
    }

    public WebElement findElementNow(By elementLocator) {
        WebElement webElement;
        synchronized (this.monitor) {
            setImplicitTimeout(100, ChronoUnit.MILLIS);
            try {
                webElement = driver.findElement(elementLocator);
            } catch (NoSuchElementException e) {
                webElement = null;
            }
            resetImplicitTimeout();
        }
        return webElement;
    }

    public void scrollPageToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void scrollRight(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollLeft = document.documentElement.scrollWidth", element);
    }

    public void scrollLeft(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollLeft = -document.documentElement.scrollWidth", element);
    }

    public void waitUntilEnabled(WebElement webElement) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((Function<WebDriver, Boolean>) o -> !webElement.getAttribute("class").contains("disabled"));
    }

    public void waitUntilEnabled(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((Function<WebDriver, Boolean>) o -> !find(by).getAttribute("class").contains("disabled"));
    }

    public <T, R> R executeWithoutImplicityWait(java.util.function.Function<T, R> function) {
        R result;
        synchronized (this.monitor) {
            this.setImplicitTimeout(100, ChronoUnit.MILLIS);
            result = function.apply(null);
            this.resetImplicitTimeout();
        }
        return result;
    }

}
