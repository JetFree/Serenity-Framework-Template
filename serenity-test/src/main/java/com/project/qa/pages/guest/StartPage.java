package com.project.qa.pages.guest;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.DefaultUrl;
import org.junit.Assert;

/**
 * Created by Evgeny.Gurinovich on 13.04.2016.
 */
@DefaultUrl("https://qa.readnaturally.com")
public class StartPage extends PageObject {

    private final String VISIT_US_TEXT = "Looking for more information about Read Live?";

    @FindBy(xpath = "//span[contains(text(), 'Student')]/..")
    private WebElementFacade studentButton;

    @FindBy(xpath = "//span[contains(text(), 'Staff')]/..")
    private WebElementFacade staffMemberButton;

    @FindBy(linkText = "Visit our website")
    private WebElementFacade visitOurWebsiteLink;

    @FindBy(css = ".visitUs")
    private WebElementFacade visitUsText;

    @FindBy(linkText = "Subscription Agreement")
    private WebElementFacade subscriptionAgreementLink;

    @FindBy(linkText = "Privacy Policy")
    private WebElementFacade policyLink;

    @FindBy(linkText = "Help")
    private WebElementFacade helpLink;

    @FindBy(linkText = "Support")
    private WebElementFacade supportLink;

    @FindBy(css = ".check-sys-reqs")
    private WebElementFacade sysRequirementsLink;

    public void clickStudentButton() {
        studentButton.click();
    }

    public void clickStaffMemberButton() {
        staffMemberButton.click();
    }

    public void clickVisitOurSiteLink() {
        visitOurWebsiteLink.click();
    }

    public void verifyVisitUsText() {
        Assert.assertTrue(String.format("'%s' text is not found. Actual text is: %s", this.VISIT_US_TEXT, visitUsText.getText()),
                visitUsText.getText().trim().contains(this.VISIT_US_TEXT));
    }

    public void openSubscriptionLink() {
        subscriptionAgreementLink.click();
    }

    public void openPolicyLink() {
        policyLink.click();
    }

    public void openHelpLink() {
        helpLink.click();
    }

    public void openSupportLink() {
        supportLink.click();
    }

    public void clickSystemRequirements() {
        sysRequirementsLink.click();
    }
}
