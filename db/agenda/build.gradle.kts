plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.lade.agenda"
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
    implementation(project(":core:database"))
    implementation(project(":core:resources"))
    implementation(project(":db:agenda-store"))
    api(project(":feature:entry-kind"))
    implementation(project(":feature:recurrence"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}