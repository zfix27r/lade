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
    api(project(":core:entry"))
    api(project(":core:goal"))
    api(project(":core:log"))
    api(project(":core:note"))

    implementation(project(":core:database"))
    implementation(project(":core:resources"))
    implementation(project(":db:agenda-store"))
    implementation(project(":feature:recurrence"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}