package com.project.qa.steps.guest;

import com.project.qa.pages.guest.StartPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.steps.ScenarioSteps;
import org.openqa.selenium.Dimension;

import java.util.concurrent.TimeUnit;

/**
 * Created by Evgeny.Gurinovich on 14.04.2016.
 */
public class AtStartPage extends ScenarioSteps {

    StartPage atStartPage;

    @Step
    public AtStartPage openStartPage() {
        atStartPage.open();
        getDriver().manage().window().setSize(new Dimension(1024, 768));
        getDriver().manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
        getDriver().manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        return this;
    }

    @Step
    public void selectStudentSection() {
        atStartPage.clickStudentButton();
    }

    @Step
    public void selectStuffMemberSection() {
        atStartPage.clickStaffMemberButton();
    }

    @Step
    public void verifyMoreInformationText() {
        atStartPage.verifyVisitUsText();
    }

    @Step
    public void openSystemRequirements() {
        atStartPage.clickSystemRequirements();
    }
}
