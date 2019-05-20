package com.readnaturally.qa.controls;

import com.readnaturally.qa.utils.DriverUtils;
import com.readnaturally.qa.utils.PropertiesLoader;
import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class contains methods to select any value
 * from dropdown list and get selected value
 *
 * @author Evgeny.Gurinovich on 30/10/18.
 */
public class SelectElementImpl extends WebElementFacadeImpl implements Select {

    private WebDriver driver;
    private ElementLocator locator;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    PropertiesLoader propertiesLoader = new PropertiesLoader();
    private long timeoutInMilSec = Long.parseLong(propertiesLoader.getProperty("serenity.timeout"));
    private WebElementFacade currentWebElement;
    private final String expandListElement = "//div[@class='Select-menu-outer']";
    private final String itemWithTextLocator = ".//div[@role='option' and text()=\"%s\"]";
    private final String optionsLocator = "//*[@role='option']";

    /**
     * Create new object with methods to work with custom select element
     *
     * @param locator - html element that has class = 'Select-value'
     * @see SelectElementImpl (WebDriver, ElementLocator, long)
     */
    public SelectElementImpl(WebDriver driver, ElementLocator locator, long implicitTimeoutInMilliseconds) {
        super(driver, locator, implicitTimeoutInMilliseconds);
        this.driver = driver;
        this.locator = locator;
        this.currentWebElement = new WebElementFacadeImpl(this.driver, this.locator, timeoutInMilSec);
    }

    @Override
    public List<String> getAllValues() {
        List<String> optionValues = new ArrayList<>();
        List<WebElementFacade> options;
        DriverUtils.getDriverUtils(this.driver).waitForAjaxComplete();
        currentWebElement.waitUntilClickable();
        this.currentWebElement.click();
        try {
            options = this.currentWebElement.thenFindAll(this.expandListElement + this.optionsLocator);
            if (options.isEmpty()) throw new NoSuchElementException("Options for select elements was not found");
            optionValues.addAll(options.stream().map(WebElementFacade::getText).collect(Collectors.toList()));
        } catch (NoSuchElementException e) {
            return this.getAllValues();
        } catch (TimeoutException e) {
            return this.getAllValues();
        }
        this.currentWebElement.click();
        logger.info("Select options are: " + Arrays.toString(optionValues.toArray()));
        return optionValues;
    }

    @Override
    public String getSelectedValue() {
        try {
            this.setImplicitTimeout(Duration.ofSeconds(0));
            return this.currentWebElement.find(By.xpath("." + this.optionsLocator)).getText();
        } catch (NoSuchElementException e) {
            logger.error("There is no selected value found in element!");
        }
        this.resetTimeouts();
        return "";
    }

    @Override
    public WebElementFacade selectByVisibleText(String label) {
        WebElementFacade listOfItems;
        DriverUtils.getDriverUtils(this.driver).waitForAjaxComplete();
        currentWebElement.waitUntilClickable();
        currentWebElement.click();
        try {
            listOfItems = currentWebElement.findBy(expandListElement);
        } catch (NoSuchElementException e) {
            currentWebElement.click();
            listOfItems = currentWebElement.findBy(expandListElement);
        }
        try {
            WebElementFacade itemToClick = listOfItems.findBy(String.format(itemWithTextLocator, label));
            DriverUtils.getDriverUtils(this.driver).scrollPageToElement(itemToClick);
            itemToClick.click();
        } catch (StaleElementReferenceException e) {
            this.selectByVisibleText(label);
        }
        return this.currentWebElement;
    }
}
