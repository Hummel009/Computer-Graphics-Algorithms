import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.multiplatform")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(8)
	}
}

kotlin {
	mingwX64 {
		binaries {
			executable {
				entryPoint("com.github.hummel.cga.lab4.main")
				linkerOpts("-lwinmm")
				baseName = "${project.name}-${project.version}"
				runTask?.standardInput = System.`in`
			}
		}
	}
	sourceSets {
		configureEach {
			languageSettings {
				optIn("kotlinx.cinterop.ExperimentalForeignApi")
			}
		}
	}
}