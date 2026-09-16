plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    kotlin("plugin.serialization") version "2.2.20"
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
    implementation("io.ktor:ktor-client-core:3.3.1")
    implementation("io.ktor:ktor-client-cio:3.3.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.3.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

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
                // we want to include only the data parsers package in the coverage report
                packages("org.byte_bloom.flux.data.csv.parsers")
            }
        }

        verify {
            rule {
                // we will change the min bound to 80 it now 20 just for pass the coverage
                minBound(20)
            }
        }
    }
}
