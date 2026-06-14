plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

group = "com.rmh"
version = property("version") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${findProperty("paperVersion")}")

    implementation("org.bstats:bstats-bukkit:3.2.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to version,
            "paperApiVersion" to (findProperty("paperApiVersion") ?: "26.1")
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
                platformVersions.set(listOf("26.1"))
                dependencies {
                    hangar("Lands") { required = false }
                    hangar("WorldGuard") { required = false }
                    hangar("CMI") { required = false }
                }
            }
        }
    }
}
