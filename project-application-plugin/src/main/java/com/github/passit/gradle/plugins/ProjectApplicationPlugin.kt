package com.github.passit.gradle.plugins

import com.github.passit.gradle.plugins.amplify.AmplifyCloudFormationImportPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

open class ProjectApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("lifecycle-base");
        project.pluginManager.apply(AmplifyCloudFormationImportPlugin::class.java)
    }
}
