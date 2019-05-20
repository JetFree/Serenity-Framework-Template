package com.readnaturally.qa.tests.ui.guest;

import com.readnaturally.qa.testcommon.SimpleTestTemplate;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.annotations.WithTagValuesOf;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Evgeny.Gurinovich on 5/3/2018.
 */
@WithTagValuesOf({"module:student", "functionality:student home page", "home page links", "type:ui"})
public class HomeLinksTest extends SimpleTestTemplate {

    @Test
    @Title("RN2-9857:More Information")
    public void verifyMoreInformationLink() {
        guest.atStartPage().openStartPage();
        guest.atStartPage().verifyMoreInformationText();
    }
}

