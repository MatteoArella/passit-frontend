buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
    }
}

plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version("1.4.21")
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.android.tools.build:gradle:4.1.1")
    // gson
    implementation("com.google.code.gson:gson:2.8.6")

    // mustache
    implementation("com.github.spullara.mustache.java:compiler:0.9.7")

    // aws sdk
    implementation(platform("software.amazon.awssdk:bom:2.15.0"))
    implementation("software.amazon.awssdk:cloudformation")
    implementation("org.apache.httpcomponents:httpclient:4.5.9")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-Werror", "-Xlint:deprecation"))
}

gradlePlugin {
    plugins {
        create("projectApplicationPlugin") {
            id = "com.github.passit.project-application"
            implementationClass = "com.github.passit.gradle.plugins.ProjectApplicationPlugin"
        }
    }
}
