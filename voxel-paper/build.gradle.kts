repositories {
    mavenCentral()
    mavenLocal()

    maven("https://leycm.github.io/repository")
    maven("https://repo.papermc.io/repository/maven-public/")
}


dependencies {
    if (project.name != "api") implementation(project(":api"))
    implementation("org.leycm.frames:the-frame:1.3.15")

    compileOnly("io.papermc.paper:paper-api:${project.properties["minecraft_version"]}-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:${project.properties["lombok_version"]}")
    annotationProcessor("org.projectlombok:lombok:${project.properties["lombok_version"]}")
    testCompileOnly("org.projectlombok:lombok:${project.properties["lombok_version"]}")
    testAnnotationProcessor("org.projectlombok:lombok:${project.properties["lombok_version"]}")
}