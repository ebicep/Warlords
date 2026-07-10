import org.gradle.api.file.DuplicatesStrategy
import java.security.MessageDigest
import java.util.zip.ZipFile
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("com.gradleup.shadow") version "8.3.5" // Creates a fat jar
    java
    `maven-publish`
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1" // Generates plugin.yml based on the Gradle config
}

group = "com.ebicep"
version = "Prelude to Chaos"
description = "Warlords"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencyLocking {
    lockMode.set(LockMode.STRICT)
}

configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
    runtimeClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

repositories {
    mavenCentral()
    google()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.infernalsuite.com/repository/maven-snapshots/")
    }

//    maven {
//        url = uri("https://repo.rapture.pw/repository/maven-releases/")
//    }

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

    implementation("net.dv8tion:JDA:5.0.0-beta.24")

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

    jar {
        enabled = false
        archiveVersion.set(project.version.toString().replace(" ", "-"))
    }

    shadowJar {
        archiveVersion.set(project.version.toString().replace(" ", "-"))
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
        mergeServiceFiles()
        filesMatching("META-INF/services/**") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
        relocate("co.aikar.commands", "com.ebicep.warlords.acf.acf")
        relocate("co.aikar.locales", "com.ebicep.warlords.acf.locales")
    }

    reobfJar {
        val safeVersion = project.version.toString().replace(" ", "-")
        val output = System.getProperty("outputDirectory")
        if (output != null) {
            outputJar.set(layout.projectDirectory.file("${output}${project.name}-${safeVersion}.jar"))
        } else {
            outputJar.set(layout.buildDirectory.file("libs/${project.name}-${safeVersion}.jar"))
        }
    }

    runServer {
        version.set("1.21.4")
    }

    register("printReobfJarPath") {
        dependsOn(reobfJar)
        doLast {
            println(reobfJar.get().outputJar.get().asFile.absolutePath)
        }
    }

    register("verifyPluginJar") {
        dependsOn(reobfJar)
        doLast {
            val jar = reobfJar.get().outputJar.get().asFile
            if (!jar.exists()) {
                throw GradleException("reobfJar output not found: ${jar.absolutePath}")
            }

            val minEntries = 40_000
            ZipFile(jar).use { zip ->
                val entries = zip.entries().asSequence().map { entry -> entry.name }.toList()
                if (entries.size < minEntries) {
                    throw GradleException("JAR has too few entries (${entries.size} < $minEntries)")
                }
                if (entries.none { name -> name.startsWith("com/ebicep/warlords/acf/") }) {
                    throw GradleException("Relocated ACF missing from JAR")
                }

                val manifestEntry = zip.getEntry("META-INF/MANIFEST.MF")
                    ?: throw GradleException("MANIFEST.MF missing from JAR")
                val manifest = zip.getInputStream(manifestEntry).bufferedReader().readText()

                if (!manifest.contains("paperweight-mappings-namespace: spigot")) {
                    throw GradleException("Expected spigot mappings namespace in manifest")
                }
            }

            val digest = MessageDigest.getInstance("SHA-256")
            jar.inputStream().use { input ->
                val buffer = ByteArray(8192)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }
            val sha256 = digest.digest().joinToString("") { byte -> "%02x".format(byte) }

            println("JAR path: ${jar.absolutePath}")
            println("JAR size: ${jar.length()} bytes")
            println("JAR SHA-256: $sha256")
        }
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


paperPluginYaml {
    main = "com.ebicep.warlords.Warlords"
    bootstrapper = "com.ebicep.warlords.WarlordsBootstrap"
    apiVersion = "1.21.4"
    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
    version = project.version.toString()
    authors = listOf("ebicep", "Plikie")
    dependencies {
        server {
            register("LuckPerms") {
                joinClasspath = true
                load = PaperPluginYaml.Load.BEFORE
            }
            register("ProtocolLib") {
                joinClasspath = true
                required = true
                load = PaperPluginYaml.Load.BEFORE
            }
            register("Citizens") {
                joinClasspath = true
                required = true
                load = PaperPluginYaml.Load.BEFORE
            }
            register("Multiverse-Core") {
                joinClasspath = true
                required = true
                load = PaperPluginYaml.Load.BEFORE
            }
        }
    }
}

