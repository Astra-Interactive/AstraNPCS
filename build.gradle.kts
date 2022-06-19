
val kotlin_version: String by project
val kotlin_coroutines_version: String by project
val kotlin_json_version: String by project
val kaml: String by project


val name = "AstraNPCS"
group = "com.astrainteractive"
version = "1.0.2"
description = "AstraNPCS"

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
//    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.essentialsx.net/snapshots/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo1.maven.org/maven2/")
    maven("https://maven.playpro.com")
    maven("https://jitpack.io")
    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
        metadataSources {
            artifact()
        }
    }
    flatDir { dirs("libs") }
}

dependencies {
    val spigot = "1.19-R0.1-SNAPSHOT"
    val placeholderapi = "2.11.1"
    val protocolLib = "4.8.0"
    val worldguard = "7.0.5"
    val vault = "1.7"
    val coreprotect = "21.2"
    val modelengine = "R2.5.0"
    val essentials = "2.19.4-SNAPSHOT"
    val discordSRV = "1.24.0"
    val luckPerms = "5.3"
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlin_coroutines_version")
    // Serialization
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version")
    implementation("com.charleskorn.kaml:kaml:$kaml")
    // AstraLibs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.20")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:1.24.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.2.1")
    testImplementation("io.kotest:kotest-assertions-core:5.2.1")
    testImplementation(kotlin("test"))

    // Spigot dependencies
    compileOnly("net.essentialsx:EssentialsX:2.19.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:$spigot")
    compileOnly("org.spigotmc:spigot-api:$spigot")
    compileOnly("org.spigotmc:spigot:$spigot")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    compileOnly("com.discordsrv:discordsrv:1.22.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.coreprotect:coreprotect:20.0")
    compileOnly("com.ticxo.modelengine:api:R2.5.0")
}

tasks{
    withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    withType<Jar> {
        archiveClassifier.set("min")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
            this.showStandardStreams = true
        }
    }
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to project.description
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}
tasks.shadowJar {
    dependencies {
        include(dependency(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", ".aar")))))
        include(dependency("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlin_coroutines_version"))
        include(dependency("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_json_version"))
        include(dependency("com.charleskorn.kaml:kaml:$kaml"))
    }
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    destinationDirectory.set(File("D:\\Minecraft Servers\\TEST_SERVER\\plugins"))
}
