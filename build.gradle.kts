plugins {
    id(Libs.Plugins.kotlinJvm) version Libs.Versions.kotlin
}

group = "org.kiworkshop.learningfpinkotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(Libs.Test.kotest)
    testImplementation(Libs.Test.kotestAssertionsCore)
    testImplementation(Libs.Test.kotestPropertyTesting)
    testImplementation(Libs.Test.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
