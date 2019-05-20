package com.project.qa.framework.listener;

import com.google.common.base.Function;
import com.project.qa.utils.DriverUtils;
import net.serenitybdd.core.annotations.findby.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by JetFree on 10.01.18.
 */
public class CustomEventListener extends AbstractWebDriverEventListener {

    private static final String BUSY_MODAL_XPATH = "//*[contains(@class, 'modal-dialog')]";
    private static final String FADE_IN_MODAL_XPATH = "//div[contains(@class, 'fade in modal') or contains(@class, 'fade modal')]";
    private static final String MODAL_HEADER_XPATH = "//div[@class='modal-header']";
    private Logger logger = LoggerFactory.getLogger(CustomEventListener.class);
    private WebDriver driver;

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        this.driver = driver;
        super.beforeClickOn(element, driver);
        waitForSpinnerAbsence(driver);
    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {
        List<LogEntry> errors = driver.manage().logs().get("browser").filter(Level.WARNING);
        if (!errors.isEmpty()) {
            logger.error("================Browser errors started======================");
            for(LogEntry entry : errors) {
                logger.error("Error found: " + entry.getMessage());
            }
            logger.error("================Browser errors finished======================");
        }
        super.onException(throwable, driver);
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {

        super.afterClickOn(element, driver);
        waitForSpinnerAbsence(driver);
    }



    public void waitForSpinnerAbsence(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(new Function<WebDriver, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable WebDriver input) {
                if (DriverUtils.getDriverUtils(driver).isElementHiddenNow(By.xpath(MODAL_HEADER_XPATH))) {
                    return DriverUtils.getDriverUtils(driver).isElementHiddenNow(By.xpath(FADE_IN_MODAL_XPATH));
                } else {
                    FluentWait fluentWait = new FluentWait(driver)
                            .withTimeout(Duration.ofSeconds(1));
                    try {
                        fluentWait.until(o -> DriverUtils.getDriverUtils(driver).isElementHiddenNow(By.xpath(FADE_IN_MODAL_XPATH)));
                    } catch (TimeoutException e) {
//                        Do nothing
                    }
                    return true;
                }
            }
        });
    }
}
