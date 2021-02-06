package com.github.passit.gradle.plugins.amplify

import com.github.passit.gradle.tasks.amplify.CloudFormationImportTask
import com.github.passit.gradle.tasks.amplify.ConfigureAmplifyTask
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

        val cloudFormationImportTask: TaskProvider<CloudFormationImportTask> =
            project.tasks.register(
                "cloudFormationImportTask", CloudFormationImportTask::class.java
            ) { task ->
                task.group = "Build"
                task.projectDirectory.set(extension.projectDirectory)
                task.backendStackName.set(extension.backendStackName)
                task.outputs.upToDateWhen { false }
            }

        val configureAmplifyTask: TaskProvider<ConfigureAmplifyTask> =
            project.tasks.register(
                "generateAmplifyConfiguration", ConfigureAmplifyTask::class.java
            ) { task ->
                task.group = "Build"
                task.projectDirectory.set(extension.projectDirectory)
                task.stackOutputs.set(cloudFormationImportTask.get().stackOutputs)
                task.amplifyTemplate.set(extension.amplifyTemplate)
                task.awsTemplate.set(extension.awsTemplate)
                task.amplifyConfigFile.set(extension.amplifyConfigFile)
                task.awsConfigFile.set(extension.awsConfigFile)
                task.dependsOn(cloudFormationImportTask)
            }

        project.tasks.named("assemble") { task -> task.dependsOn(configureAmplifyTask) }
    }
}