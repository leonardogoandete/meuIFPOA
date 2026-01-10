pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    plugins {
        id("com.android.application") version "8.13.2" apply false
        id("com.android.library") version "8.13.2" apply false
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.multiplatform") version "2.0.0" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
        id("org.jetbrains.compose") version "1.7.1" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "meuIFPOA"
include(":app")
include(":baselineprofile")
include(":shared")
