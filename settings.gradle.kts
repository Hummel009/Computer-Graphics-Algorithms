pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "latest.release"
}

include(":appLab1J")
include(":appLab1N")
include(":appLab2J")
include(":appLab2N")
include(":appLab3J")
include(":appLab3N")
include(":appLab4J")
include(":appLab5-1J")
include(":appLab5-2J")
include(":appLab5-3J")
include(":appLab5-4J")