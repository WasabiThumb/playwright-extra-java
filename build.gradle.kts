
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
}

dependencies {
    api("com.microsoft.playwright:playwright:$baseVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
