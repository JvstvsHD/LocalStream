import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.30"
}

group = "de.jvstvshd.localstream"
version = "1.0.0-beta"

val nettyVersion = "4.1.66.Final"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty", "netty-all", nettyVersion)
    implementation("com.google.guava", "guava", "30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.reflections", "reflections", "0.9.12")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("net.kyori:event-api:3.0.0")
    implementation("net.bytebuddy:byte-buddy:1.11.15")
    //audio
    //implementation("com.googlecode.soundlibs", "tritonus-share", "0.3.7.4")

    implementation("com.googlecode.soundlibs", "mp3spi", "1.9.5-1")
    implementation("com.googlecode.soundlibs", "jlayer", "1.0.1-1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    implementation("org:jaudiotagger:2.0.3")
    implementation("javazoom:jlayer:1.0.1")
    // https://mvnrepository.com/artifact/com.googlecode.soundlibs/tritonus-share
    implementation("com.googlecode.soundlibs:tritonus-share:0.3.7.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("net.lingala.zip4j:zip4j:2.9.0")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.jvstvshd.localstream"
            artifactId = "common"
            version = "1.0.0-alpha"

            from(components["java"])
        }
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "16"
}