package org.squidmin.cucumber.skeleton;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class StepDefinitions extends SpringIntegrationTest {

    @Autowired
    private Belly belly;

    @Given("I have {int} cukes in my belly")
    public void I_have_cukes_in_my_belly(int cukes) {
        belly.eat(cukes);
    }

    @When("I wait 1 hour")
    public void i_wait_1_hour() {

    }

    @Then("my belly should growl")
    public void my_belly_should_growl() {

    }

}
