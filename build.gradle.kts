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
}

tasks.withType<Test> {
    useJUnitPlatform()
}
