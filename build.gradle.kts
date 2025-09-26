plugins {
    kotlin("jvm") version "2.1.0"
    java
    `maven-publish`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "xapi"
        )
    }
}

group = "net.botwithus.xapi"
version = "2.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        setUrl("https://nexus.botwithus.net/repository/maven-snapshots/")
    }
}

dependencies {
    implementation("net.botwithus.api:api:1.+")
    implementation("net.botwithus.imgui:imgui:1.+")
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("botwithus.navigation:nav-api:1.0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")

    // Logging dependencies
    implementation("org.slf4j:slf4j-api:2.0.9")
}

publishing {
    repositories {
        maven {
            url = if (project.version.toString().endsWith("SNAPSHOT")) {
                uri("https://nexus.botwithus.net/repository/maven-snapshots/")
            } else {
                uri("https://nexus.botwithus.net/repository/maven-releases/")
            }
            credentials {
                username = System.getenv("MAVEN_REPO_USER")
                password = System.getenv("MAVEN_REPO_PASS")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            
            pom {
                name.set("BotWithUs XAPI")
                description.set("Extended API framework for BotWithUs RuneScape 3 bot development")
                
                properties.set(mapOf(
                    "maven.compiler.source" to "24",
                    "maven.compiler.target" to "24"
                ))
            }
        }
    }
}