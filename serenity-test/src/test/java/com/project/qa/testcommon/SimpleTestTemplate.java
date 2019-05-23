package com.project.qa.testcommon;

import com.project.qa.apisteps.StaffApiSteps;
import com.project.qa.steps.GuestUserSteps;
import com.project.qa.utils.db.DbExecutor;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static com.project.qa.utils.db.DbExecutor.connect;

/**
 * Created by Evgeny.Gurinovich on 14.04.2016.
 */
@RunWith(SerenityRunner.class)
public class SimpleTestTemplate {

    @Managed()
    public WebDriver driver;

    @ManagedPages
    public Pages pages;

    @Steps
    protected GuestUserSteps guest;

    @Steps
    public static StaffApiSteps staffApi;


    @AfterClass
    public static void closeDbConnection() {
        connect().to(DbExecutor.DB.DATABASE).closeConnection();
    }
}
