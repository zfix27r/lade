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

include(":core:database")
include(":core:resources")
include(":core:ui")

include(":db:agenda")
include(":db:agenda-store")
include(":db:category-store")
include(":db:temporal-store")
include(":db:reminders-store")
include(":db:notification-store")
include(":db:chat-store")

include(":feature:draft")
include(":feature:feed")
include(":feature:calendar")
include(":feature:chat")
include(":feature:analytics")
include(":feature:reminders")
include(":feature:synccalendar")
include(":feature:syncdevices")
include(":feature:habits")
include(":feature:time")
include(":feature:categories")
include(":feature:recurrence")
include(":feature:scenarios")
include(":feature:planer")
include(":feature:settings")
include(":feature:more")
include(":feature:entryDetails")
include(":feature:entry-kind")
include(":feature:daypart")
include(":feature:notifications")
include(":feature:schedule")
