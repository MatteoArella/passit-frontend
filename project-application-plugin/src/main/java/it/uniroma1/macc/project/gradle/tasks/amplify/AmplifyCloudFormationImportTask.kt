package it.uniroma1.macc.project.gradle.tasks.amplify

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

    @InputFile
    val amplifyTemplate: Property<File> = project.objects.property(File::class.java)

    @InputFile
    val awsTemplate: Property<File> = project.objects.property(File::class.java)

    @InputFile
    @Optional
    val cdkOutputsFile: Property<File> = project.objects.property(File::class.java)

    @Input
    val backendStackName: Property<String> = project.objects.property(String::class.java)

    @OutputFile
    val amplifyConfigFile: Property<File> = project.objects.property(File::class.java)

    @OutputFile
    val awsConfigFile: Property<File> = project.objects.property(File::class.java)

    @TaskAction
    @Throws(IOException::class)
    fun configureAmplify() {
        val outputs: Map<String, String> = if (cdkOutputsFile.isPresent) {
            logger.info(
                String.format(
                    "retrieving backend %s stack outputs from cdk outputs file %s",
                    backendStackName.get(),
                    cdkOutputsFile.get().absolutePath
                )
            )
            val json = JsonParser.parseString(
                String(Files.readAllBytes(cdkOutputsFile.get().toPath()), StandardCharsets.UTF_8)
            )
            val outputMapType = object : TypeToken<Map<String, String>?>() {}.type
            Gson().fromJson(json.asJsonObject[backendStackName.get()], outputMapType)
        } else {
            logger.info(
                String.format(
                    "retrieving backend %s stack outputs from cloudformation service",
                    backendStackName.get()
                )
            )
            val client = CloudFormationClient.builder().build()
            val response = client.describeStacks(
                DescribeStacksRequest.builder().stackName(backendStackName.get()).build()
            )
            response.stacks()[0].outputs().associateBy({ it.outputKey()}, { it.outputValue() })
        }
        val mf: MustacheFactory = DefaultMustacheFactory()
        var mustache = mf.compile(
            BufferedReader(FileReader(amplifyTemplate.get())),
            "amplifyconfiguration"
        )
        FileWriter(amplifyConfigFile.get()).use { writer ->
            mustache.execute(writer, outputs)
            writer.flush()
        }
        mustache = mf.compile(
            BufferedReader(FileReader(awsTemplate.get())),
            "awsconfiguration"
        )
        FileWriter(awsConfigFile.get()).use { writer ->
            mustache.execute(writer, outputs)
            writer.flush()
        }
    }
}