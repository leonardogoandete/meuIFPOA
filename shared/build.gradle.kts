plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

compose {
    resources {
        generateResClass = always
    }
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                // Lifecycle ViewModel for KMP
                implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

                // Ktor for networking
                implementation("io.ktor:ktor-client-core:2.3.12")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
                implementation("io.ktor:ktor-client-logging:2.3.12")

                // Firebase KMP
                implementation("dev.gitlive:firebase-auth:1.11.1")
                implementation("dev.gitlive:firebase-firestore:1.11.1")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:2.3.12")
                implementation("androidx.activity:activity-compose:1.9.0")
                implementation("androidx.appcompat:appcompat:1.7.0")
                implementation("androidx.core:core-ktx:1.15.0")

                // Compose dependencies for UI
                implementation("androidx.compose.material:material-icons-extended:1.7.6")
                implementation("androidx.compose.ui:ui-tooling-preview:1.7.6")

                // Navigation Compose
                implementation("androidx.navigation:navigation-compose:2.8.0")

                // Credential Manager & Google Sign-In (Android-only)
                implementation("androidx.credentials:credentials:1.3.0-alpha02")
                implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha02")
                implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.12")
            }
        }
    }
}

android {
    namespace = "br.com.ifrs.meuifpoa.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
