package com.github.passit.gradle.plugins.amplify

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

open class AmplifyCloudFormationImportExtension @Inject constructor(
    projectLayout: ProjectLayout,
    objectFactory: ObjectFactory
) {
    val projectDirectory: DirectoryProperty
    val amplifyConfigFile: Property<File>
    val amplifyTemplate: Property<File>
    val backendStackName: Property<String>

    init {
        val configFilesPrefixDir = "src/main/res/raw"

        this.projectDirectory = objectFactory.directoryProperty().apply { set(projectLayout.projectDirectory) }
        this.amplifyConfigFile = objectFactory.property(File::class.java).apply { set(projectLayout.projectDirectory.file(String.format("%s/amplifyconfiguration.json", configFilesPrefixDir)).asFile) }
        this.amplifyTemplate = objectFactory.property(File::class.java)
        this.backendStackName = objectFactory.property(String::class.java)
    }

    fun setAmplifyConfigFile(file: File) {
        this.amplifyConfigFile(file)
    }

    fun amplifyConfigFile(file: File) {
        this.amplifyConfigFile.set(file)
    }

    fun setAmplifyTemplate(file: File) {
        this.amplifyTemplate(file)
    }

    fun amplifyTemplate(file: File) {
        this.amplifyTemplate.set(file)
    }

    fun setBackendStackName(backendStackName: String) {
        this.backendStackName(backendStackName)
    }

    fun backendStackName(backendStackName: String) {
        this.backendStackName.set(backendStackName)
    }
}
