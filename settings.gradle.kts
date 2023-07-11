
pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    versionCatalogs {
        create("libs") {
            from("no.nordicsemi.android.gradle:version-catalog:1.7.0")
        }
    }
}

rootProject.name = "Blek-UI"

include(":app_blek_client")
include(":app_blek_mock")
include(":app_blek_server")
include(":app_commons")
include(":uiscanner")

if (file("../Kotlin-BLE-Library").exists()) {
    includeBuild("../Kotlin-BLE-Library")
}

if (file("../Android-Common-Libraries").exists()) {
    includeBuild("../Android-Common-Libraries")
}
