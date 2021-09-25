object Libs {
    object Plugins {
        const val kotlinJvm = "org.jetbrains.kotlin.jvm"
    }

    object Versions {
        const val kotlin = "1.5.30"
        const val kotest = "4.6.2"
        const val mockk = "1.12.0"
    }

    object Test {
        const val kotest = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
        const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${Versions.kotest}"
        const val kotestPropertyTesting = "io.kotest:kotest-property:${Versions.kotest}"
        const val mockk = "io.mockk:mockk:${Versions.mockk}"
    }
}
