package com.project.qa.controls;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by evgeny.gurinovich on 8/9/2017.
 */
public class Table extends WebElementFacadeImpl implements TableElement {

    public Table(WebDriver driver, ElementLocator locator, long implicitTimeoutInMilliseconds) {
        super(driver, locator, implicitTimeoutInMilliseconds);
    }

    @Override
    public List<List<String>> getTableMap() {
        List<List<String>> resultTableList = new ArrayList<>();
        List<WebElement> trElements = this.getElement().findElements(By.xpath(".//tbody//tr"));
        for (WebElement tr : trElements) {
            List<String> values = new ArrayList<>();
            List<WebElement> tdElements = tr.findElements(By.xpath(".//td"));
            values.addAll(tdElements.stream().map(WebElement::getText).collect(Collectors.toList()));
            resultTableList.add(values);
        }
        return resultTableList;
    }
}
