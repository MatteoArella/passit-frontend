package com.github.passit.gradle.plugins

import com.android.build.api.dsl.*
import com.github.passit.gradle.plugins.amplify.AmplifyCloudFormationImportPlugin
import com.github.passit.gradle.tasks.amplify.CloudFormationImportTask
import org.gradle.api.Plugin
import org.gradle.api.Project

open class ProjectApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("lifecycle-base")
        project.pluginManager.apply("com.android.application")
        project.pluginManager.apply("kotlin-android")
        project.pluginManager.apply(AmplifyCloudFormationImportPlugin::class.java)

        val cloudFormationImportTask = project.tasks.getByName("cloudFormationImportTask") as CloudFormationImportTask

        project.tasks.getByName("preBuild").dependsOn(cloudFormationImportTask)

        project.extensions.getByType(CommonExtension::class.java).apply {
            onVariantProperties {
                manifestPlaceholders.set(cloudFormationImportTask.stackOutputs)
            }
        }
    }
}
