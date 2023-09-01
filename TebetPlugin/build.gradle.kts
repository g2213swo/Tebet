plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.g2213swo"
version = "0.0.1"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    testImplementation("org.mockito:mockito-junit-jupiter:3.12.4")
    testImplementation("org.mockito:mockito-core:3.12.4")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly(group = "com.comphenix.protocol", name = "ProtocolLib", version = "5.1.0")

    implementation(project(":TebetAPI"))
    implementation("com.jayway.jsonpath:json-path:2.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

}

tasks.build {
    dependsOn("shadowJar")
}
