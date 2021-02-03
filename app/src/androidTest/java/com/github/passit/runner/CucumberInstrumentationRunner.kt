package com.github.passit.runner

import android.os.Bundle
import com.github.passit.BuildConfig
import cucumber.api.android.CucumberAndroidJUnitRunner;

@SuppressWarnings("unused")
class CucumberInstrumentationRunner : CucumberAndroidJUnitRunner() {
    private val CUCUMBER_TAGS_KEY : String = "tags"
    private val CUCUMBER_SCENARIO_KEY : String = "name"

    override fun onCreate(bundle: Bundle) {
        val tags: String = BuildConfig.TEST_TAGS
        var scenario: String = BuildConfig.TEST_SCENARIO

        if (tags.isNotEmpty()) {
            val tagsKey = tags.replace("\\s".toRegex(), "")
            bundle.putString(CUCUMBER_TAGS_KEY, tagsKey)
        }

        if (scenario.isNotEmpty()) {
            scenario = scenario.replace(" ".toRegex(), "\\\\s")
            bundle.putString(CUCUMBER_SCENARIO_KEY, scenario)
        }

        super.onCreate(bundle)
    }
}
