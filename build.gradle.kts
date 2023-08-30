subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            url = uri("https://repo.dmulloy2.net/repository/public/")
        }
    }
}
