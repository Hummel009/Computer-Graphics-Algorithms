import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm") version "latest.release"
	id("application")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

val embed: Configuration by configurations.creating

dependencies {
	embed("org.jetbrains.kotlin:kotlin-stdlib:latest.release")
	embed("com.formdev:flatlaf:latest.release")
	embed("com.formdev:flatlaf-intellij-themes:latest.release")
	implementation("com.formdev:flatlaf:latest.release")
	implementation("com.formdev:flatlaf-intellij-themes:latest.release")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(8)
	}
}

application {
	mainClass = "com.github.hummel.cga.lab4j.MainKt"
}

tasks {
	named<JavaExec>("run") {
		standardInput = System.`in`
	}
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "com.github.hummel.cga.lab4j.MainKt"
				)
			)
		}
		from(embed.map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}