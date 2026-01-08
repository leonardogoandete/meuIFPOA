plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    id("androidx.baselineprofile") version "1.2.4"
}

android {
    namespace = "br.com.ifrs.meuifpoa.baselineprofile"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.enableSourceSetToPackageMap"] = true
}

dependencies {
    // Dependências para os testes de macrobenchmark.
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("junit:junit:4.13.2")
    implementation("androidx.test.ext:junit:1.2.1")
    implementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0")
    implementation("androidx.benchmark:benchmark-macro-junit4:1.2.4")
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
}
