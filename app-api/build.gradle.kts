plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "app.lade.app_api"
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
    implementation(libs.androidx.navigation.compose)
}