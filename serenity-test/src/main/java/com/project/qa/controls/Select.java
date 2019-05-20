package com.project.qa.controls;

import net.serenitybdd.core.annotations.ImplementedBy;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.List;

/**
 * Created by Evgeny.Gurinovich on 10/30/2018.
 */
@ImplementedBy(SelectElementImpl.class)
public interface Select extends WebElementFacade {

    String getSelectedValue();

    List<String> getAllValues();
}
