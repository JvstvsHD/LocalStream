import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"
    kotlin("jvm") version "1.5.30"
}

group = "de.jvstvshd.localstream"
version = "1.0-SNAPSHOT"

val log4jVersion = "2.14.1"
val nettyVersion = "4.1.66.Final"

repositories {
    mavenCentral()
    mavenLocal()
}

application {
    mainModule.set("de.jvstvshd.localstream.client")
    mainClass.set("de.jvstvshd.localstream.client.LocalStreamClient")
}

javafx {
    version = "16"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

dependencies {
    //logging
    implementation("org.apache.logging.log4j", "log4j-api", log4jVersion)
    implementation("org.apache.logging.log4j", "log4j-core", log4jVersion)

    //network & database
    implementation("io.netty", "netty-all", nettyVersion)
    implementation("de.jvstvshd.localstream:api:1.0.0-alpha")

    //utilities
    implementation("com.google.guava", "guava", "30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("net.sf.jopt-simple:jopt-simple:6.0-alpha-2")

    implementation("com.googlecode.soundlibs", "mp3spi", "1.9.5-1")
    implementation("com.googlecode.soundlibs", "jlayer", "1.0.1-1")
    implementation("com.googlecode.soundlibs", "tritonus-share", "0.3.7.4")


    //audio
    /*implementation("com.googlecode.soundlibs", "mp3spi", "1.9.5-1")
    implementation("com.googlecode.soundlibs", "jlayer", "1.0.1-1")
    implementation("com.googlecode.soundlibs", "tritonus-share", "0.3.7.4")*/
    //implementation("org:jaudiotagger:2.0.3")
    //implementation("com.googlecode.soundlibs", "vorbisspi", "1.0.3-1")

    /*implementation("org.controlsfx:controlsfx:11.1.0")
    implementation("com.dlsc.formsfx:formsfx-core:11.4.2")
    implementation("net.synedra:validatorfx:0.1.13") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.2.0")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:16.0.3")*/

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "16"
}