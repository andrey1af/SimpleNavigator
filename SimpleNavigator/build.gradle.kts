plugins {
    id("application")
    id("java")
    id("io.freefair.lombok") version "8.6"

}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.projectlombok:lombok:1.18.42")
}

application {
    mainClass = "Main"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}

val libsDir = layout.buildDirectory.dir("libs")

tasks.register<Jar>("queueJar") {
    group = "library"
    description = "Creates JAR for S21_queue library"
    archiveBaseName.set("s21_queue")
    archiveVersion.set("")

    from(sourceSets.main.get().output) {
        include("S21_collection/Queue.class")
        include("S21_collection/Collection.class")
        include("S21_collection/Node.class")
    }

    destinationDirectory.set(libsDir)
    dependsOn(tasks.compileJava)
}

tasks.register<Jar>("stackJar") {
    group = "library"
    description = "Creates JAR for S21_stack library"
    archiveBaseName.set("s21_stack")
    archiveVersion.set("")

    from(sourceSets.main.get().output) {
        include("S21_collection/Stack.class")
        include("S21_collection/Collection.class")
        include("S21_collection/Node.class")
    }

    destinationDirectory.set(libsDir)
    dependsOn(tasks.compileJava)
}

tasks.register<Jar>("graphJar") {
    group = "library"
    description = "Creates JAR for Graph library"
    archiveBaseName.set("graph")
    archiveVersion.set("")

    from(sourceSets.main.get().output) {
        include("graph/**")
    }

    destinationDirectory.set(libsDir)
    dependsOn(tasks.compileJava)
}

tasks.register<Jar>("algorithmsJar") {
    group = "library"
    description = "Creates JAR for GraphAlgorithms library"
    archiveBaseName.set("graph_algorithms")
    archiveVersion.set("")

    from(sourceSets.main.get().output) {
        include("algorithms/**")
        include("data/**")
    }

    destinationDirectory.set(libsDir)
    dependsOn(tasks.compileJava)
}

tasks.register("buildLibs") {
    group = "library"
    description = "Builds all static libraries"
    dependsOn("queueJar", "stackJar", "graphJar", "algorithmsJar")

    doLast {
        println("All libraries built successfully!")
        println("Libraries location: ${libsDir.get().asFile.absolutePath}")
        libsDir.get().asFile.listFiles()?.filter { it.extension == "jar" }?.forEach {
            println("  - ${it.name}")
        }
    }
}

// Fat JAR с использованием библиотек
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Creates executable fat JAR using static libraries"
    archiveBaseName.set("SimpleNavigator")
    archiveVersion.set("")
    archiveClassifier.set("all")

    manifest {
        attributes["Main-Class"] = "Main"
    }

    from(sourceSets.main.get().output)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    destinationDirectory.set(libsDir)
    dependsOn(tasks.compileJava)
}

tasks.register<Jar>("appJar") {
    group = "build"
    description = "Creates main application JAR (requires library JARs in classpath)"
    archiveBaseName.set("app")
    archiveVersion.set("")

    manifest {
        attributes["Main-Class"] = "Main"
        attributes["Class-Path"] = "s21_queue.jar s21_stack.jar graph.jar graph_algorithms.jar"
    }

    from(sourceSets.main.get().output) {
        include("Main.class")
        include("View.class")
        include("View\$*.class")
    }

    destinationDirectory.set(libsDir)
    dependsOn(tasks.compileJava)
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds all libraries and main application"
    dependsOn("buildLibs", "appJar")

    doLast {
        println("\n=== Build Complete ===")
        println("To run the application:")
        println("  java -cp build/libs/*: Main")
        println("Or use: ./gradlew run")
    }
}

tasks.register<Delete>("cleanLibs") {
    group = "library"
    description = "Cleans only library JARs"
    delete(fileTree(libsDir) {
        include("s21_queue.jar", "s21_stack.jar", "graph.jar", "graph_algorithms.jar", "app.jar")
    })
}