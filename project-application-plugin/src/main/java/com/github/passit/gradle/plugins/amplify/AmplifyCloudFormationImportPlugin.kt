package com.github.passit.gradle.plugins.amplify

import com.github.passit.gradle.tasks.amplify.AmplifyCloudFormationImportTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

open class AmplifyCloudFormationImportPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("lifecycle-base")

        val extension: AmplifyCloudFormationImportExtension = project.extensions.create(
            "amplify", AmplifyCloudFormationImportExtension::class.java,
            project.layout,
            project.objects
        )

        val amplifyCloudFormationImportTask: TaskProvider<AmplifyCloudFormationImportTask> =
            project.tasks.register(
                "generateAmplifyConfiguration", AmplifyCloudFormationImportTask::class.java
            ) { task ->
                task.group = "Build"
                task.projectDirectory.set(extension.projectDirectory)
                task.amplifyTemplate.set(extension.amplifyTemplate)
                task.awsTemplate.set(extension.awsTemplate)
                task.backendStackName.set(extension.backendStackName)
                task.amplifyConfigFile.set(extension.amplifyConfigFile)
                task.awsConfigFile.set(extension.awsConfigFile)
            }

        project.tasks.named("assemble") { task -> task.dependsOn(amplifyCloudFormationImportTask) }
    }
}