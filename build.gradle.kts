import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.spotless)

    application
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.json)

    testImplementation(libs.kotlin.test)
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}


tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.languageVersion = "1.9"
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(mapOf("disabled_rules" to "no-wildcard-imports"))
    }
}


application {
    mainClass.set("MainKt")
}