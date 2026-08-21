plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt.android)
}

fun countTaskWaveFiles(): Int {
	val lade = file("src/main/java/app/lade")
	if (!lade.isDirectory) return 1
	return lade.walkTopDown()
		.filter { it.isFile && it.name.matches(Regex("""tasks-v\d+\.md""")) }
		.count()
		.coerceAtLeast(1)
}

android {
	namespace = "app.lade"
	compileSdk = 37

	defaultConfig {
		applicationId = "app.lade"
		minSdk = 26
		targetSdk = 37
		versionCode = countTaskWaveFiles()
		versionName = "0.1.0"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	kotlinOptions {
		jvmTarget = "17"
	}

	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.compose.material3)
	implementation("androidx.compose.material:material-icons-extended")
	implementation(libs.androidx.navigation.compose)

	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)

	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation.compose)
	implementation(libs.biweekly)

	debugImplementation(libs.androidx.compose.ui.tooling)
}
