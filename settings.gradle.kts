pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://mvn.lastcrash.io/releases")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://mvn.lastcrash.io/releases")
    }
}

rootProject.name = "LastCrash Sample App Kotlin"
include(":app")
 