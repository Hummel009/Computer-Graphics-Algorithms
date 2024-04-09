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
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":appLab1")
include(":appLab1J")
include(":appLab2")
include(":appLab2J")
include(":appLab3")
include(":appLab3J")
include(":appLab4J")
include(":appLab5-1J")
include(":appLab5-2J")
include(":appLab5-3J")
include(":appLab5-4J")