plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.lade.calendardata"
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
    implementation(project(":core:entry"))
    implementation(project(":core:goal"))

    implementation(project(":db:agenda-store"))

    implementation(project(":feature:humanize"))
    implementation(project(":feature:recurrence"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}