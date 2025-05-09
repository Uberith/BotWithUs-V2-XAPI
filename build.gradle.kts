plugins {
    `maven-publish`
}

group = "net.botwithus.xapi"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":api"))
    implementation(project(":imgui"))
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