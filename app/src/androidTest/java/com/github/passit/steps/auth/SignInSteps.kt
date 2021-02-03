package com.github.passit.steps.auth

import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import com.github.passit.robots.auth.SignInRobot

class SignInSteps {
    private val signInRobot = SignInRobot()

    @Given("I am on the Sign In screen")
    fun unauthenticatedUserOnSignInScreen() {
        signInRobot.launchSignInScreen()
    }

    @And("I enter user email (\\S+)")
    fun enterUserEmail(email: String) {
        signInRobot.enterUserEmail(email)
    }

    @And("I enter user password (\\S+)")
    fun enterUserPassword(password: String) {
        signInRobot.enterUserPassword(password)
    }

    @And("I press the Sign In button")
    fun pressSignInButton() {
        signInRobot.pressSignInButton()
    }

    @Then("I expect to see the \"Main Application\" screen")
    fun seeMainApplicationScreen() {
        signInRobot.checkMainActivityIntent()
    }
}