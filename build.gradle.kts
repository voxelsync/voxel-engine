import java.util.*

plugins {
    java
}

subprojects {
    apply(plugin = "java")

    fun generateSuffix(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        return (1..6)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    tasks.named<Jar>("jar") {
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
            line.replace("\${version}", project.version.toString())
                .replace("\${group}", project.group.toString())
                .replace("\${name}", project.name)
        }


        include("**/*.yml", "**/*.txt", "**/*.properties", "**/*.json")
        expand(
            "version" to project.version,
            "group" to project.group,
            "name" to project.name,
        )
    }

    tasks.register("deployJar") {
        dependsOn("jar")

        doLast {
            val jarTask = tasks.getByName<Jar>("jar")
            val jarFile = jarTask.archiveFile.get().asFile

            println(jarFile.path.toString())

            val targetDir = file("${rootProject.rootDir}/out/${project.name}/")
            val targetName = "${project.name}-v${project.version}#${generateSuffix()}.jar"

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            copy {
                from(jarFile)
                into(targetDir)
                rename { targetName }
            }

        }

        group = "deployment"
        description = "Build the JAR and saves it to /out/{project.name}"

    }

}