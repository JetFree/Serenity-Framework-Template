package com.readnaturally.qa.testcommon;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.pages.Pages;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Created by Evgeny.Gurinovich on 22.04.2016.
 */
@RunWith(SerenityParameterizedRunner.class)
public abstract class ParameterizedTestTemplate extends SimpleTestTemplate {

    @Managed
    public WebDriver driver;

    @ManagedPages
    public Pages pages;


}
