package it.uniroma1.macc.project.test

import cucumber.api.CucumberOptions

@CucumberOptions(
    features = ["features" ],
    plugin = ["pretty"],
    glue = ["it.uniroma1.macc.project.steps"],
    tags = ["@ui, @smoke, @acceptance"]
)
@SuppressWarnings("unused")
class CucumberRunner {
}
