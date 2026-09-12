plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt.android)
}

fun countTaskWaveFiles(): Int {
	val roots = listOf(
		file("src/main/java/app/lade"),
		rootProject.file("feed/src/main/java/app/lade"),
		rootProject.file("database/src/main/java/app/lade"),
	)
	return roots.sumOf { root ->
		if (!root.isDirectory) 0
		else root.walkTopDown()
			.filter { it.isFile && it.name.matches(Regex("""tasks-v\d+\.md""")) }
			.count()
	}.coerceAtLeast(1)
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

	buildFeatures {
		compose = true
	}
}

// AGP 9 may omit empty androidTest aggregates; older IDE actions still request them.
tasks.register("androidTestClasses") {
	description = "No-op shim for IDEs expecting the AGP 8 androidTestClasses task."
}

// Архитектурная проверка (ArchGuard) запускается первой при сборке приложения.
tasks.named("preBuild") {
	dependsOn(":validateArchitecture")
}

dependencies {
	implementation(project(":analytics"))
	implementation(project(":calendar"))
	implementation(project(":categories"))
	implementation(project(":chat"))
	implementation(project(":database"))
	implementation(project(":entry"))
	implementation(project(":feed"))
	implementation(project(":habits"))
	implementation(project(":more"))
	implementation(project(":notifications"))
	implementation(project(":reminders"))
	implementation(project(":resources"))
	implementation(project(":scenarios"))
	implementation(project(":settings"))
	implementation(project(":synccalendar"))
	implementation(project(":syncdevices"))
	implementation(project(":temporal"))
	implementation(project(":time"))
	implementation(project(":ui"))

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
	implementation(libs.androidx.navigation.compose)

	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation.compose)

	debugImplementation(libs.androidx.compose.ui.tooling)
}
