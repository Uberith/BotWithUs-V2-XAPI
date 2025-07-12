plugins {
    kotlin("jvm") version "2.1.0"
    java
    `maven-publish`
}

group = "net.botwithus.xapi"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        setUrl("https://nexus.botwithus.net/repository/maven-snapshots/")
    }
}

dependencies {
    implementation("net.botwithus.api:api:1.0.0-SNAPSHOT")
    implementation("net.botwithus.imgui:imgui:1.0.0-SNAPSHOT")
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("com.google.code.gson:gson:2.10.1")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}