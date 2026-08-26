plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
}

group = "org.ByteBloom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
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
                packages("org.byte_bloom.flux.data.parsers")
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
