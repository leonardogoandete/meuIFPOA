import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.dokka") version "1.8.10"
}

android {
    namespace = "br.com.ifrs.meuifpoa"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.ifrs.meuifpoa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21  // Atualizado para Java 21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
    }

    buildToolsVersion = "35.0.0"
}

tasks.dokkaHtml {
    outputDirectory.set(file("../docs/"))
    dokkaSourceSets {
        create("main") {
            sourceRoots.from(file("src/main/java"))

            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/"))
            }
        }
    }
}

// Adicione o plugin "kotlin-as-java-plugin"
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.firebase.firestore)
    implementation(libs.glide)
    implementation(libs.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dependências do Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("androidx.credentials:credentials:1.3.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("br.com.caelum.stella:caelum-stella-core:2.1.6")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.material:material:1.12.0")


    // Adiciona o plugin kotlin-as-java para que a documentação seja em Java
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
}
