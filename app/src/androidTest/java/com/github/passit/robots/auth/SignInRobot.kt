package com.github.passit.robots.auth

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.passit.ui.screens.auth.SignInActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.github.passit.R
import com.github.passit.ui.screens.main.MainActivity
import org.junit.Rule

class SignInRobot {
    private val signInActivityScenario = ActivityScenarioRule(SignInActivity::class.java)

    @Rule
    @JvmField
    val intentsScenarioRule = IntentsTestRule(SignInActivity::class.java)

    fun launchSignInScreen() {
        signInActivityScenario.scenario.moveToState(Lifecycle.State.CREATED)
    }

    fun enterUserEmail(email: String) {
        onView(withId(R.id.emailTextField)).perform(typeText(email), closeSoftKeyboard())
    }

    fun enterUserPassword(password: String) {
        onView(withId(R.id.passwordTextField)).perform(typeText(password), closeSoftKeyboard())
    }

    fun pressSignInButton() {
        onView(withId(R.id.signInBtn)).perform(click())
    }

    fun checkMainActivityIntent() {
        intended(hasComponent(MainActivity::class.java.name))
    }
}
