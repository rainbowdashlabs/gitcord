import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
    java
}

group = "dev.chojo"
version = "1.0.0"

repositories {
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}


spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        target("**/*.java")
    }
}

dependencies {
    //discord
    implementation("de.chojo", "cjda-util", "2.9.6+beta.19") {
        exclude(group = "club.minnced", module = "opus-java")
    }

    // database
    implementation("org.postgresql", "postgresql", "42.7.1")
    implementation(libs.bundles.sadu)

    // Logging
    implementation(libs.bundles.log4j)
    implementation("de.chojo", "log-util", "1.0.1") {
        exclude("org.apache.logging.log4j")
    }

    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("version") {
                expand(
                    "version" to project.version
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        transform(Log4j2PluginsCacheFileTransformer::class.java)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "de.chojo.repbot.ReputationBot"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
