plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.lade.backup"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:resources"))
    implementation(project(":core:database"))
    implementation(project(":db:agenda-store"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}