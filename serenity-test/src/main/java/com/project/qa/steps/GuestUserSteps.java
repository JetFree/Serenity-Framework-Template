package com.project.qa.steps;

import com.project.qa.steps.guest.AtStartPage;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;

/**
 * Created by Evgeny.Gurinovich on 13.04.2016.
 */
public class GuestUserSteps extends ScenarioSteps {

    private StepFactory stepFactory;

    public GuestUserSteps(Pages pages) {
        super(pages);
        stepFactory = new StepFactory(pages);
    }

    public AtStartPage atStartPage() {
        return stepFactory.getNewStepLibraryFor(AtStartPage.class);
    }

}
