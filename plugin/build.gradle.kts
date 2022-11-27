val pluginName = "AstraNPCS"
group = Dependencies.group
version = Dependencies.version
description = "AstraNPCS"

plugins {
    java
    `maven-publish`
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}
java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
repositories {
    mavenLocal()
    mavenCentral()
    maven(Dependencies.Repositories.extendedclip)
    maven(Dependencies.Repositories.maven2Apache)
    maven(Dependencies.Repositories.essentialsx)
    maven(Dependencies.Repositories.enginehub)
    maven(Dependencies.Repositories.spigotmc)
    maven(Dependencies.Repositories.dmulloy2)
    maven(Dependencies.Repositories.papermc)
    maven(Dependencies.Repositories.dv8tion)
    maven(Dependencies.Repositories.playpro)
    maven(Dependencies.Repositories.jitpack)
    maven(Dependencies.Repositories.scarsz)
    maven(Dependencies.Repositories.maven2)
    modelEngige(project)
    astraLibs(project)
    paperMC(project)
}

dependencies {
    // Kotlin
    implementation(Dependencies.Libraries.kotlinGradlePlugin)
    // Coroutines
    implementation(Dependencies.Libraries.kotlinxCoroutinesCoreJVM)
    implementation(Dependencies.Libraries.kotlinxCoroutinesCore)
    // Serialization
    implementation(Dependencies.Libraries.kotlinxSerialization)
    implementation(Dependencies.Libraries.kotlinxSerializationJson)
    implementation(Dependencies.Libraries.kotlinxSerializationYaml)
    // AstraLibs
    implementation(Dependencies.Libraries.astraLibsKtxCore)
    implementation(Dependencies.Libraries.astraLibsSpigotCore)
    // Test
    testImplementation(kotlin("test"))
    testImplementation(Dependencies.Libraries.orgTeting)
    // Spigot dependencies
    compileOnly(Dependencies.Libraries.essentialsX)
    compileOnly(Dependencies.Libraries.paperMC)
    compileOnly(Dependencies.Libraries.spigot)
    compileOnly(Dependencies.Libraries.spigotApi)
    compileOnly(Dependencies.Libraries.protocolLib)
    compileOnly(Dependencies.Libraries.placeholderapi)
    compileOnly(Dependencies.Libraries.worldguard)
    compileOnly(Dependencies.Libraries.discordsrv)
    compileOnly(Dependencies.Libraries.vaultAPI)
    compileOnly(Dependencies.Libraries.coreprotect)
    compileOnly(Dependencies.Libraries.modelengine)

    implementation(project(":remote-api"))
    implementation(project(":versioned:core"))
}

tasks {
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
                    "name" to pluginName,
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
        // Kotlin
        include(dependency(Dependencies.Libraries.kotlinGradlePlugin))
        // Coroutines
        include(dependency(Dependencies.Libraries.kotlinxCoroutinesCoreJVM))
        include(dependency(Dependencies.Libraries.kotlinxCoroutinesCore))
        // Serialization
        include(dependency(Dependencies.Libraries.kotlinxSerialization))
        include(dependency(Dependencies.Libraries.kotlinxSerializationJson))
        include(dependency(Dependencies.Libraries.kotlinxSerializationYaml))
    }
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    this.archiveBaseName.set(pluginName)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    destinationDirectory.set(File(Dependencies.destinationDirectoryPath))
}
