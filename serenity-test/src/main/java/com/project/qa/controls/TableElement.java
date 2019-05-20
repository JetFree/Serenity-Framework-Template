package com.project.qa.controls;

import net.serenitybdd.core.annotations.ImplementedBy;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.List;

/**
 * Created by evgeny.gurinovich on 8/9/2017.
 */
@ImplementedBy(Table.class)
public interface TableElement extends WebElementFacade {

    List<List<String>> getTableMap();
}
