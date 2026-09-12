plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt.android)
}

android {
	namespace = "app.lade.feed"
	compileSdk = 37

	defaultConfig {
		minSdk = 26
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.material3)

	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)

	testImplementation(libs.junit)
	testImplementation(libs.kotlin.test)
	testImplementation(libs.kotlin.reflect)
}
