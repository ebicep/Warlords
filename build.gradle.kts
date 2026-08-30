import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml
import java.time.Instant

plugins {
    id("com.gradleup.shadow") version "9.3.1" // Creates a fat jar
    java
    `maven-publish`
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "3.0.2" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1" // Generates plugin.yml based on the Gradle config
}

group = "com.ebicep"
version = "Drowned Realms"
description = "Warlords"

val archiveVersionSuffix: String
    get() = version.toString().replace(" ", "-")

fun gitOutput(vararg args: String): String = runCatching {
    providers.exec {
        commandLine(listOf("git") + args)
        isIgnoreExitValue = true
    }.standardOutput.asText.get().trim().ifEmpty { "unknown" }
}.getOrElse { "unknown" }

val gitCommit: String = gitOutput("rev-parse", "HEAD")
val gitCommitShort: String = gitOutput("rev-parse", "--short", "HEAD")
val gitBranch: String = gitOutput("rev-parse", "--abbrev-ref", "HEAD")
val gitCommitTime: String = gitOutput("log", "-1", "--format=%cI")
val gitDirty: String = runCatching {
    providers.exec {
        commandLine("git", "status", "--porcelain")
        isIgnoreExitValue = true
    }.standardOutput.asText.get().isNotBlank().toString()
}.getOrElse { "false" }
val buildTime: String = Instant.now().toString()

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    mavenCentral()
    google()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven {
        name = "citizens-repo"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo")
    maven("https://repo.onarandombox.com/content/groups/public/")
}

dependencies {
    pluginRemapper("net.fabricmc:tiny-remapper:0.12.1:fat")
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    implementation("co.aikar:taskchain-bukkit:3.7.2")

    implementation("net.dv8tion:JDA:5.0.0-beta.24")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.0.4")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    implementation("de.rapha149.signgui:signgui:2.5.4")

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.18")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("it.unimi.dsi:fastutil:8.5.12")

    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }

    compileOnly("com.comphenix.protocol:ProtocolLib:5.4.0-SNAPSHOT")

    compileOnly("net.luckperms:api:5.4")

    compileOnlyApi("me.libraryaddict.disguises:libsdisguises:11.0.8") {
        exclude("org.spigotmc", "spigot")
    }

    implementation("fr.skytasul:guardianbeam:2.4.6")

    testImplementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.110.0")
    testImplementation("org.objenesis:objenesis:3.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

paperweight {
    addServerDependencyTo = configurations.named("compileOnly").map { setOf(it) }
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

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        expand(
            mapOf(
                "projectVersion" to project.version.toString(),
                "gitCommit" to gitCommit,
                "gitCommitShort" to gitCommitShort,
                "gitBranch" to gitBranch,
                "gitCommitTime" to gitCommitTime,
                "gitDirty" to gitDirty,
                "buildTime" to buildTime,
            )
        )
    }

    jar {
        archiveVersion.set(archiveVersionSuffix)
    }

    shadowJar {
        archiveVersion.set(archiveVersionSuffix)
        relocate("co.aikar.commands", "com.ebicep.warlords.acf.acf")
        relocate("co.aikar.locales", "com.ebicep.warlords.acf.locales")

        dependencies {
            exclude(dependency("club.minnced:opus-java"))
        }
    }

    reobfJar {
        val output = System.getProperty("outputDirectory")
        if (output != null) {
            outputJar.set(layout.buildDirectory.file("${output}${project.name}-${archiveVersionSuffix}.jar"))
        } else {
            outputJar.set(layout.buildDirectory.file("libs/${project.name}-${archiveVersionSuffix}.jar"))
        }
    }

    runServer {
        version.set("1.21.4")
    }

    test {
        useJUnitPlatform()
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = Charsets.UTF_8.name()
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.release.set(21)
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

paperPluginYaml {
    main = "com.ebicep.warlords.Warlords"
    bootstrapper = "com.ebicep.warlords.WarlordsBootstrap"
    apiVersion = "1.21.11"
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
            register("LibsDisguises") {
                joinClasspath = true
                required = true
                load = PaperPluginYaml.Load.BEFORE
            }
        }
    }
}
