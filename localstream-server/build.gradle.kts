plugins {
    java
}

group = "de.jvstvshd.localstream"
version = "1.0.0.0"

val log4jVersion = "2.14.1"
val nettyVersion = "4.1.66.Final"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    //logging
    implementation("org.apache.logging.log4j", "log4j-api", log4jVersion)
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", log4jVersion)

    //config
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")

    //network & database
    implementation("io.netty", "netty-all", nettyVersion)
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("de.jvstvshd.localstream:api:1.0.0-alpha");
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.2")

    implementation("com.google.guava", "guava", "30.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

   //audio
    implementation("com.googlecode.soundlibs", "mp3spi", "1.9.5-1")
    implementation("com.googlecode.soundlibs", "jlayer", "1.0.1-1")
    implementation("com.googlecode.soundlibs", "tritonus-share", "0.3.7.4")

    //test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")


}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}