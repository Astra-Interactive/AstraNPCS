
import java.util.Properties
import java.io.FileInputStream

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.5.21"
}
var gprUser:String? = null
var gprPassword:String? = null
task("Load GPR keys"){
    val astraPropsFile = file("astra.properties")
    if (!astraPropsFile.exists())
        astraPropsFile.createNewFile()
    val astraProps = Properties().apply { load(FileInputStream(astraPropsFile)) }
    gprUser = astraProps.getProperty("gpr.user")
    gprPassword = astraProps.getProperty("gpr.password")
    if (gprUser == null || gprPassword == null) {
        if (gprUser == null)
            astraProps.setProperty("gpr.user", "SET_GPR_USERNAME_HERE")
        if (gprPassword == null)
            astraProps.setProperty("gpr.password", "SET_GPR_KEY_HERE")
        astraProps.store(astraPropsFile.outputStream(), "")
        throw GradleException("You need to set your GPR keys")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.maven.apache.org/maven2/")
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Astra-Interactive/AstraLibs")
        credentials {
            username = gprUser
            password = gprPassword
        }
        metadataSources {
            artifact()
        }
    }
    maven("https://repo1.maven.org/maven2/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation("com.astrainteractive:astralibs:1.1.6")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("me.clip:placeholderapi:2.10.9")
    testImplementation("junit:junit:4.13.1")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:1.24.1")
    testImplementation("io.kotest:kotest-runner-junit5:latest.release")
    testImplementation("io.kotest:kotest-assertions-core:latest.release")
    testImplementation(kotlin("test"))
}

group = "com.astrainteractive"
version = "1.0.1"
description = "AstraNPCS"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
