package com.github.passit.test

import cucumber.api.CucumberOptions

@CucumberOptions(
    features = ["features" ],
    plugin = ["pretty"],
    glue = ["com.github.passit.steps"],
    tags = ["@ui, @smoke, @acceptance"]
)
@SuppressWarnings("unused")
class CucumberRunner {
}
