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

	implementation(project(":db:agenda"))
	implementation(project(":core:resources"))
	implementation(project(":core:ui"))

	implementation(project(":db:category-store"))
	implementation(project(":db:chat-store"))
    implementation(project(":db:agenda-store"))


    implementation(project(":feature:analytics"))
	implementation(project(":feature:calendar"))
	implementation(project(":feature:categories"))
	implementation(project(":feature:chat"))
	implementation(project(":feature:entryDetails"))
	implementation(project(":feature:feed"))
	implementation(project(":feature:habits"))
	implementation(project(":feature:more"))
	implementation(project(":feature:notifications"))
	implementation(project(":feature:reminders"))
	implementation(project(":feature:scenarios"))
	implementation(project(":feature:settings"))
	implementation(project(":feature:synccalendar"))
	implementation(project(":feature:syncdevices"))
	implementation(project(":feature:recurrence"))
	implementation(project(":feature:time"))

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.activity.compose)

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.navigation.compose)
	implementation("androidx.compose.material:material-icons-extended")

	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)

	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation.compose)

	debugImplementation(libs.androidx.compose.ui.tooling)
}
