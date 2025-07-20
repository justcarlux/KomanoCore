plugins {
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("com.mysql:mysql-connector-j:9.3.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.4")
}

tasks {
    runServer {
        minecraftVersion("1.21.7")
    }
    jar {
        archiveBaseName = "komano-core"
    }
    shadowJar {
        archiveFileName = "komano-core-${project.version}-all.jar"
        relocate("co.aikar.commands", "me.carlux.komanocore.acf")
        relocate("co.aikar.locales", "me.carlux.komanocore.acflocales")
        relocate("com.zaxxer", "me.carlux.komanocore.hikaricp")
    }
}

paper {
    name = "KomanoCore"
    description = "Quality of life stuff for a chill Survival server"
    main = "me.carlux.komanocore.PaperPlugin"
    version = "1.0.0"
    author = "JustCarluX"
    apiVersion = "1.21.7"
}