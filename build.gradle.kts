plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

group = "com.rmh"
version = property("version") as String

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + configurations.testCompileClasspath.get()
        runtimeClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
    }
}

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
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("io.papermc.paper:paper-api:${findProperty("paperVersion")}")

    add("integrationTestImplementation", platform("org.junit:junit-bom:5.10.2"))
    add("integrationTestImplementation", "org.junit.jupiter:junit-jupiter")
    add("integrationTestRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    add("integrationTestImplementation", "io.papermc.paper:paper-api:${findProperty("paperVersion")}")
    add("integrationTestImplementation", "me.clip:placeholderapi:2.11.6")
    add("integrationTestImplementation", "org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.54.0")
    add("integrationTestImplementation", files("../theryn-plugin-test-utils/build/libs/theryn-plugin-test-utils-1.0.0.jar"))
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

    register<Test>("integrationTest") {
        description = "Runs MockBukkit integration tests."
        group = JavaBasePlugin.VERIFICATION_GROUP
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        useJUnitPlatform()
        shouldRunAfter(test)
    }

    check {
        dependsOn("integrationTest")
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        id.set("ItemMagnets")
        channel.set("Release")
        changelog.set("""
            v1.2.2 — Optional proximity lore: coordinate zones whisper ambient messages when holding an active magnet. Config GUI zone editor. Off by default.
            See CHANGELOG.md on GitHub.
            """.trim())
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
