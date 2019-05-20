package com.project.qa.apisteps;

import com.project.qa.utils.PropertiesLoader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.StepFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.rest.SerenityRest.given;

/**
 * Created by evgeny.gurinovich on 7/27/2017.
 */
public class StaffApiSteps extends ApiSteps {

    StepFactory stepFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(StaffApiSteps.class);
    private static PropertiesLoader propertiesLoader = new PropertiesLoader();
    protected String token;
    protected String staffId;


    public StaffApiSteps(Pages pages) {
        super(pages);
        this.stepFactory = new StepFactory(pages);
        RestAssured.baseURI = propertiesLoader.getProperty("student.api.base.url");
    }

    @Step
    protected Response getResponse() {
        Response response = given().contentType(ContentType.JSON)
                .headers(new Headers(new Header("some header", "some value"),
                        new Header("Authorization", "Bearer " + this.token)))
                .log().all()
                .get("Some url");
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to receive parameter. Failed with HTTP error code: " + response.getStatusCode());
        }
        return response;
    }

}
