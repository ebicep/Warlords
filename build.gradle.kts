import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("com.gradleup.shadow") version "8.3.5" // Creates a fat jar
    java
    `maven-publish`
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0" // Generates plugin.yml based on the Gradle config
}

group = "com.ebicep"
version = "Echoes of Demise"
description = "Warlords"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    mavenCentral()
    google()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.infernalsuite.com/repository/maven-snapshots/")
    }

    maven {
        url = uri("https://repo.rapture.pw/repository/maven-releases/")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }

    maven {
        name = "citizens-repo"
        url = uri("https://maven.citizensnpcs.co/repo")
    }

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/groups/public/")
    }


    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo")
    }

    maven {
        url = uri("https://repo.onarandombox.com/content/groups/public/")
    }

}

dependencies {
    pluginRemapper("net.fabricmc:tiny-remapper:0.10.4:fat")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")

    implementation("co.aikar:taskchain-bukkit:3.7.2")

    implementation("net.dv8tion:JDA:4.4.0_350")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.0.4")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

//    implementation("com.github.Rapha149.SignGUI:signgui:5232fbd3f6")
    implementation("de.rapha149.signgui:signgui:2.5.0")

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("it.unimi.dsi:fastutil:8.5.12")

    compileOnly("net.citizensnpcs:citizens-main:2.0.37-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }

    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    compileOnly("net.luckperms:api:5.4")

    compileOnlyApi("LibsDisguises:LibsDisguises:10.0.44") {
        exclude("org.spigotmc", "spigot")
    }

    compileOnly("com.onarandombox.multiversecore:multiverse-core:4.3.16")

    implementation("fr.skytasul:guardianbeam:2.4.0")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}


tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
    }


    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        relocate("co.aikar.commands", "com.ebicep.warlords.acf.acf")
        relocate("co.aikar.locales", "com.ebicep.warlords.acf.locales")
    }

    build {
        dependsOn(shadowJar)
    }


    reobfJar {
        // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
        // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
        val output = System.getProperty("outputDirectory")
        if (output != null) {
            outputJar.set(layout.buildDirectory.file("${output}${project.name}-${project.version}.jar"))
        }
    }

    runServer {
        version.set("1.21.4")
    }

}

tasks.withType<JavaCompile>().configureEach {
//    doFirst {
//        configure(options, closureOf<CompileOptions> {
//            configure(forkOptions, closureOf<ForkOptions> {
//                executable = null
//                javaHome = null
//            })
//        })
//    }
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.release = 21
//    options.forkOptions.executable = "javac"
}

bukkitPluginYaml {
    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
    main = "com.ebicep.warlords.Warlords"
    apiVersion = "1.21.4"
    authors = listOf("ebicep", "Plikie")
    depend = listOf("ProtocolLib", "Citizens", "Multiverse-Core")
    commands {
        register("oldtest") {
            aliases = listOf("oldtest")
            description = "Old test command"
            permission = "group.administrator"
        }
    }
}


