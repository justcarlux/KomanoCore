plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")

    group = "me.carlux"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    }

    tasks {
        compileJava {
            options.release = 21
        }
        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }
        shadowJar {
            mergeServiceFiles()
        }
        assemble {
            dependsOn(shadowJar)
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")
    }
}