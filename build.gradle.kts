import com.vanniktech.maven.publish.SonatypeHost

val baseMinorVersion: Int = 52
val basePatchVersion: Int = 0
val extraMinorVersion: Int = 1
val extraPatchVersion: Int = 0

val baseVersion: String = "1.${baseMinorVersion}.${basePatchVersion}"
val extraVersion: String = "1.${baseMinorVersion}.${extraMinorVersion}.${extraPatchVersion}"

allprojects {
    apply(plugin = "java-library")

    group = "io.github.wasabithumb"
    version = extraVersion

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.1")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        toolchain.languageVersion = JavaLanguageVersion.of(8)
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = Charsets.UTF_8.name()
        (options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
    }
}

plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.32.0"
}

description = "Modular plugin framework for Playwright"

dependencies {
    api("com.microsoft.playwright:playwright:$baseVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("${project.group}", "playwright-extra", "${project.version}")
    pom {
        name.set("Playwright Extra")
        description.set(project.description!!)
        inceptionYear.set("2025")
        url.set("https://github.com/WasabiThumb/playwright-extra-java")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("wasabithumb")
                name.set("Xavier Pedraza")
                url.set("https://github.com/WasabiThumb/")
            }
        }
        scm {
            url.set("https://github.com/WasabiThumb/playwright-extra-java/")
            connection.set("scm:git:git://github.com/WasabiThumb/playwright-extra-java.git")
        }
    }
}
