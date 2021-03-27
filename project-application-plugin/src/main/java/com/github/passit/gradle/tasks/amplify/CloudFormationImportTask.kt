package com.github.passit.gradle.tasks.amplify

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest
import java.io.IOException

open class CloudFormationImportTask : DefaultTask() {
    @Internal
    val projectDirectory: DirectoryProperty = project.objects.directoryProperty()

    @Internal
    val stackOutputs: MapProperty<String, String> = project.objects.mapProperty(String::class.java, String::class.java)

    @Input
    val backendStackName: Property<String> = project.objects.property(String::class.java)

    @TaskAction
    @Throws(IOException::class)
    fun cloudFormationImport() {
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
            stackOutputs.putAll(response.stacks()[0].outputs().associateBy({ it.outputKey()}, { it.outputValue() }))
        }
    }
}