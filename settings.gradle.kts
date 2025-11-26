pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // don't use repositoriesMode.set(...) here to avoid the RepositoriesMode unresolved error
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Asha_Jyoti_Doctor_App"
include(":app")
