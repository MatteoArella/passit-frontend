package com.github.passit.gradle.tasks.amplify

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.*

open class ConfigureAmplifyTask : DefaultTask() {
    @Internal
    val projectDirectory: DirectoryProperty = project.objects.directoryProperty()

    @Input
    val stackOutputs: MapProperty<String, String> = project.objects.mapProperty(String::class.java, String::class.java)

    @InputFile
    val amplifyTemplate: Property<File> = project.objects.property(File::class.java)

    @InputFile
    val awsTemplate: Property<File> = project.objects.property(File::class.java)

    @OutputFile
    val amplifyConfigFile: Property<File> = project.objects.property(File::class.java)

    @OutputFile
    val awsConfigFile: Property<File> = project.objects.property(File::class.java)

    @TaskAction
    @Throws(IOException::class)
    fun configureAmplify() {
        logger.info("writing amplify configurations")
        val mf: MustacheFactory = DefaultMustacheFactory()
        var mustache = mf.compile(
            BufferedReader(FileReader(amplifyTemplate.get())),
            "amplifyconfiguration"
        )
        FileWriter(amplifyConfigFile.get()).use { writer ->
            mustache.execute(writer, stackOutputs.get())
            writer.flush()
        }
        mustache = mf.compile(
            BufferedReader(FileReader(awsTemplate.get())),
            "awsconfiguration"
        )
        FileWriter(awsConfigFile.get()).use { writer ->
            mustache.execute(writer, stackOutputs.get())
            writer.flush()
        }
    }
}