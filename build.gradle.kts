plugins {
    jacoco

    id(Libs.Plugins.kotlinJvm) version Libs.Versions.kotlin
    id(Libs.Plugins.ktlint) version Libs.Versions.ktlint
    id(Libs.Plugins.ktlintIdea) version Libs.Versions.ktlint

    id("com.github.dawnwords.jacoco.badge") version "0.2.4"
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

tasks.ktlintFormat.get().group = "verification"
tasks.ktlintCheck.get().group = "other"

tasks.check {
    dependsOn(tasks.ktlintFormat)
}

configure<JacocoPluginExtension> {
    toolVersion = Libs.Versions.jacoco
}

tasks.withType<JacocoReport> {
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }
}

tasks.withType<JacocoCoverageVerification> {
    violationRules {
        rule {
            element = "BUNDLE"

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.0".toBigDecimal()
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    finalizedBy(tasks.generateJacocoBadge)
}

val jacocoExcludePatterns = listOf("**/*Application.class", "**/*ApplicationKt.class", "**/Constants.class")

listOf(JacocoCoverageVerification::class, JacocoReport::class).forEach { taskType ->
    tasks.withType(taskType) {
        afterEvaluate {
            classDirectories.setFrom(
                files(
                    classDirectories.files.map { file ->
                        fileTree(file).apply {
                            exclude(jacocoExcludePatterns)
                        }
                    }
                )
            )
        }
    }
}
