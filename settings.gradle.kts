pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

rootProject.name = "lade"
include(":app")
include(":feed")
include(":database")
include(":calendar")
include(":chat")
include(":analytics")
include(":reminders")
include(":synccalendar")
include(":syncdevices")
include(":habits")
include(":time")
include(":categories")
include(":temporal")
include(":entry")
include(":scenarios")
include(":resources")
include(":planer")
include(":settings")
include(":more")
include(":ui")
include(":entryDetails")
include(":daypart")
include(":notifications")
include(":schedule")
