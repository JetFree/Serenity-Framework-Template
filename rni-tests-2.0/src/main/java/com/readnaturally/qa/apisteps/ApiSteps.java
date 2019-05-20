package com.readnaturally.qa.apisteps;

import com.readnaturally.qa.utils.PropertiesLoader;
import io.restassured.RestAssured;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.core.steps.StepFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 * Created by Evgeny Gurinovich on 06/07/17.
 */
public class ApiSteps extends ScenarioSteps {

    StepFactory stepFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSteps.class);
    private static PropertiesLoader propertiesLoader = new PropertiesLoader();
    protected String apiKey;


    public ApiSteps(Pages pages) {
        super(pages);
        this.stepFactory = new StepFactory(pages);
        RestAssured.baseURI = propertiesLoader.getProperty("student.api.base.url");
    }

    /**
     * Here can be any base methods for whole api
     * @return something that used in many api methods
     */
    protected String generateAccessKey() {
        this.apiKey = "abrakadabra";
        LOGGER.info("apiKey: " + this.apiKey);
        return this.apiKey;
    }
}
