import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
	id("application")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

val embed: Configuration by configurations.creating

dependencies {
	embed(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

	embed("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")

	embed("com.formdev:flatlaf:3.4.1")
	embed("com.formdev:flatlaf-intellij-themes:3.4.1")

	implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

	implementation("com.formdev:flatlaf:3.4.1")
	implementation("com.formdev:flatlaf-intellij-themes:3.4.1")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(8)
	}
}

application {
	mainClass = "com.github.hummel.cga.lab5j.MainLwjglKt"
}

tasks {
	named<JavaExec>("run") {
		standardInput = System.`in`
	}
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "com.github.hummel.cga.lab5j.MainLwjglKt"
				)
			)
		}
		from(embed.map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}