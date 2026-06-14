plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

group = "com.rmh"
version = property("version") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${findProperty("paperVersion")}")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("org.bstats:bstats-bukkit:3.2.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("io.papermc.paper:paper-api:${findProperty("paperVersion")}")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to version,
            "paperApiVersion" to (findProperty("paperApiVersion") ?: "1.21")
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveBaseName.set("ItemMagnet")
        archiveClassifier.set("")

        // Official bStats Gradle setup: https://bstats.org/getting-started
        configurations.set(listOf(project.configurations.runtimeClasspath.get()))
        dependencies {
            exclude { dependency -> dependency.moduleGroup != "org.bstats" }
        }
        relocate("org.bstats", project.group.toString())
    }

    jar {
        archiveClassifier.set("plain")
    }

    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        id.set("ItemMagnet")
        channel.set("Release")
        changelog.set("See CHANGELOG.md on GitHub.")
        apiKey.set(System.getenv("HANGAR_API_TOKEN") ?: "")

        platforms {
            paper {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.21.1", "1.21.4", "26.1"))
                dependencies {
                    hangar("Lands") { required = false }
                    hangar("WorldGuard") { required = false }
                    hangar("CMI") { required = false }
                    hangar("Towny") { required = false }
                    hangar("GriefPrevention") { required = false }
                    hangar("PlaceholderAPI") { required = false }
                }
            }
        }
    }
}
