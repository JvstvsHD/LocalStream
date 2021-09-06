plugins {
    java
    `maven-publish`
}

group = "de.jvstvshd.localstream"
version = "1.0.0-beta"

val nettyVersion = "4.1.66.Final"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.netty", "netty-all", nettyVersion)
    implementation("com.google.guava", "guava", "30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.reflections", "reflections", "0.9.12")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("net.kyori:event-api:3.0.0")
    implementation("net.bytebuddy:byte-buddy:1.11.9")
    //audio
    //implementation("com.googlecode.soundlibs", "tritonus-share", "0.3.7.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("org:jaudiotagger:2.0.3")
    implementation("javazoom:jlayer:1.0.1")
    // https://mvnrepository.com/artifact/com.googlecode.soundlibs/tritonus-share
    implementation("com.googlecode.soundlibs:tritonus-share:0.3.7.4")


}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.jvstvshd.localstream"
            artifactId = "api"
            version = "1.0.0-alpha"

            from(components["java"])
        }
    }
}