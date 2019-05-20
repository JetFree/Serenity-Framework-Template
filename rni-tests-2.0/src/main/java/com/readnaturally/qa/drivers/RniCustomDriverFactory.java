package com.readnaturally.qa.drivers;

import com.readnaturally.qa.framework.listener.CustomEventListener;
import com.readnaturally.qa.utils.PropertiesLoader;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * Created by JetFree on 10.01.18.
 */
public class RniCustomDriverFactory implements DriverSource {
    private PropertiesLoader propertiesLoader = new PropertiesLoader();

    @Override
    public WebDriver newDriver() {
        String browserName = propertiesLoader.getProperty("webdriver.browser");
        EventFiringWebDriver driver = new EventFiringWebDriver(this.initializeWebDriver(browserName));
        driver.register(new CustomEventListener());
        return driver;
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }

    protected WebDriver initializeWebDriver(String browserName) {
        WebDriver driver;
        switch (browserName.toUpperCase()) {

            case "CHROME" :
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-setuid-sandbox");
                chromeOptions.addArguments("--disable-browser-side-navigation");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "FIREFOX" : driver = new FirefoxDriver();
                break;
            case "SAFARI" : driver = new SafariDriver();
                break;
            default:
                throw new RuntimeException("Suitable driver was not found. Change browser name!");
        }
        return driver;
    }

}
