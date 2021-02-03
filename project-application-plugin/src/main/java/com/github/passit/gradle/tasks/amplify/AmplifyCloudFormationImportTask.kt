package com.github.passit.gradle.tasks.amplify

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files

open class AmplifyCloudFormationImportTask : DefaultTask() {
    @Internal
    val projectDirectory: DirectoryProperty = project.objects.directoryProperty()

    @Internal
    lateinit var stackOutputs: Map<String, String>

    @InputFile
    val amplifyTemplate: Property<File> = project.objects.property(File::class.java)

    @InputFile
    val awsTemplate: Property<File> = project.objects.property(File::class.java)

    @Input
    val backendStackName: Property<String> = project.objects.property(String::class.java)

    @OutputFile
    val amplifyConfigFile: Property<File> = project.objects.property(File::class.java)

    @OutputFile
    val awsConfigFile: Property<File> = project.objects.property(File::class.java)

    @TaskAction
    @Throws(IOException::class)
    fun configureAmplify() {
        logger.info(
            String.format(
                "retrieving backend %s stack outputs from cloudformation service",
                backendStackName.get()
            )
        )
        CloudFormationClient.builder().build().use { client ->
            val response = client.describeStacks(
                DescribeStacksRequest.builder().stackName(backendStackName.get()).build()
            )
            stackOutputs = response.stacks()[0].outputs().associateBy({ it.outputKey()}, { it.outputValue() })
        }

        val mf: MustacheFactory = DefaultMustacheFactory()
        var mustache = mf.compile(
            BufferedReader(FileReader(amplifyTemplate.get())),
            "amplifyconfiguration"
        )
        FileWriter(amplifyConfigFile.get()).use { writer ->
            mustache.execute(writer, stackOutputs)
            writer.flush()
        }
        mustache = mf.compile(
            BufferedReader(FileReader(awsTemplate.get())),
            "awsconfiguration"
        )
        FileWriter(awsConfigFile.get()).use { writer ->
            mustache.execute(writer, stackOutputs)
            writer.flush()
        }
    }
}