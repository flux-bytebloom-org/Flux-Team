plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

group = "org.ByteBloom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
}
tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

kover {
    reports {
        filters {
            includes {
                packages("org.byte_bloom.flux.data.csv.parsers")
            }
        }

        verify {
            rule {
                minBound(20)
            }
        }
    }
}
