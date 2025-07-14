plugins {
    java
    `maven-publish`
}

rootProject.version = "1.0.1"

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    fun getVersionCounterFile(type: String): File {
        return file("${rootProject.rootDir}/.idea/version-counters/${project.name}-${type}.txt")
    }

    fun getVersionCounter(type: String): Int {
        val counterFile = getVersionCounterFile(type)
        val currentVersionKey = "${type}-${rootProject.version}-last"

        if (!counterFile.exists()) {
            counterFile.parentFile.mkdirs()
            counterFile.writeText("$currentVersionKey:000000")
            return 0
        }

        val content = counterFile.readText().trim()
        val (key, counterStr) = content.split(":")
        val counter = counterStr.toInt()

        if (key != currentVersionKey) {
            counterFile.writeText("$currentVersionKey:000000")
            return 0
        }

        return counter
    }

    fun incrementVersionCounter(type: String): Int {
        val counterFile = getVersionCounterFile(type)
        val currentVersionKey = "${type}-${rootProject.version}-last"
        val newCounter = getVersionCounter(type) + 1
        val formattedCounter = String.format("%06d", newCounter)

        counterFile.writeText("$currentVersionKey:$formattedCounter")
        return newCounter
    }

    fun getProjectDisplayName(): String {
        return when (project.name) {
            "api" -> "VoxelEngineAPI"
            "paper" -> "VoxelEngine"
            else -> "VoxelEngine${project.name.capitalize()}"
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "sync.voxel.engine."
            artifactId = description
            version = rootProject.version as String?
        }

    }

    tasks.named<Jar>("jar") {
        archiveFileName.set("voxel-tmp.jar")

        from(configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) })

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest {
            attributes["Main-Class"] = "${project.group}.${project.name}.Main"
        }

        destinationDirectory.set(file("${rootDir}/out/tmp/${project.name}"))
    }

    tasks.processResources {
        from(sourceSets.main.get().resources.srcDirs)

        into(layout.buildDirectory.dir("resources/main"))

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        filter { line ->
            line.replace("\${version}", rootProject.version.toString())
                .replace("\${group}", project.group.toString())
                .replace("\${name}", project.name)
        }

        include("**/*.yml", "**/*.txt", "**/*.properties", "**/*.json")
        expand(
            "version" to rootProject.version,
            "group" to project.group,
            "name" to project.name,
        )
    }

    // Latest JAR deployment (ohne Versionsnummer)
    tasks.register("deployJar") {
        dependsOn("jar")

        doLast {
            val jarTask = tasks.getByName<Jar>("jar")
            val jarFile = jarTask.archiveFile.get().asFile

            val latestDir = file("${rootProject.rootDir}/out/latest/")
            val displayName = getProjectDisplayName()
            val targetName = "${displayName}-latest.jar"

            if (!latestDir.exists()) {
                latestDir.mkdirs()
            }

            copy {
                from(jarFile)
                into(latestDir)
                rename { targetName }
            }

            delete(layout.buildDirectory)
            println("Deployed latest JAR: ${latestDir}/${targetName}")
        }

        group = "deployment"
        description = "Build the JAR and saves it as latest version to /out/latest/"
    }

    // Development JAR deployment
    tasks.register("deployDevJar") {
        dependsOn("deployJar")

        doLast {
            val jarTask = tasks.getByName<Jar>("jar")
            val jarFile = jarTask.archiveFile.get().asFile

            val counter = incrementVersionCounter("dev")
            val formattedCounter = String.format("%06d", counter)

            val targetDir = file("${rootProject.rootDir}/out/builds/${project.name}/")
            val displayName = getProjectDisplayName()
            val targetName = "${displayName}-dev-${rootProject.version}-${formattedCounter}.jar"

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            copy {
                from(jarFile)
                into(targetDir)
                rename { targetName }
            }

            delete(layout.buildDirectory)
            println("Deployed dev JAR: ${targetDir}/${targetName}")
        }

        group = "deployment"
        description = "Build the JAR and saves it as development version"
    }

    // Alpha JAR deployment
    tasks.register("deployAlphaJar") {
        dependsOn("deployJar")

        doLast {
            val jarTask = tasks.getByName<Jar>("jar")
            val jarFile = jarTask.archiveFile.get().asFile

            val counter = incrementVersionCounter("alpha")
            val formattedCounter = String.format("%06d", counter)

            val targetDir = file("${rootProject.rootDir}/out/builds/${project.name}/")
            val displayName = getProjectDisplayName()
            val targetName = "${displayName}-alpha-${rootProject.version}-${formattedCounter}.jar"

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            copy {
                from(jarFile)
                into(targetDir)
                rename { targetName }
            }

            delete(layout.buildDirectory)
            println("Deployed alpha JAR: ${targetDir}/${targetName}")
        }

        group = "deployment"
        description = "Build the JAR and saves it as alpha version"
    }

    // Release JAR deployment
    tasks.register("deployReleaseJar") {
        dependsOn("deployJar")

        doLast {
            val jarTask = tasks.getByName<Jar>("jar")
            val jarFile = jarTask.archiveFile.get().asFile

            val counter = incrementVersionCounter("release")
            val formattedCounter = String.format("%06d", counter)

            val targetDir = file("${rootProject.rootDir}/out/builds/${project.name}/")
            val displayName = getProjectDisplayName()
            val targetName = "${displayName}-release-${rootProject.version}-${formattedCounter}.jar"

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            copy {
                from(jarFile)
                into(targetDir)
                rename { targetName }
            }

            delete(layout.buildDirectory)
            println("Deployed release JAR: ${targetDir}/${targetName}")
        }

        group = "deployment"
        description = "Build the JAR and saves it as release version"
    }
}