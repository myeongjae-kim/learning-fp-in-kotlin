package org.kiworkshop.learningfpinkotlin.sample

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify

class FooTest : StringSpec() {
    @MockK
    lateinit var fooMock: Foo

    init {
        MockKAnnotations.init(this)

        afterEach {
            confirmVerified(fooMock)
        }

        "helloWorld" {
            Foo("bar").helloWorld() shouldBe "Hello, world!"
        }

        "bar" {
            Foo("bar").bar shouldBe "bar"
        }

        "mockk" {
            // given
            every { fooMock.helloWorld() } returns "mocked"

            // when, then
            fooMock.helloWorld() shouldBe "mocked"

            verify { fooMock.helloWorld() }
        }
    }
}
