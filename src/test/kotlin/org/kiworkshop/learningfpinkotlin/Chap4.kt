package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class Chap4 : StringSpec({
    "Example 4-1" {
        // test code 4-16
        val condition: (Int) -> Boolean = { it.rem(2) == 0 }
        val body: (Int) -> String = { "$it is even" }

        val isEven = body.toPartialFunction(condition)

        isEven.isDefinedAt(100).shouldBeTrue()
        isEven(100) shouldBe "100 is even"

        // test invokeOrElse
        val oddNumber = 99
        isEven.invokeOrElse(oddNumber, "$oddNumber is odd") shouldBe "99 is odd"

        // test orElse
        val isEvenOrOdd = isEven.orElse(PartialFunction({ it.rem(2) == 1 }, { "$it is odd" }))
        isEvenOrOdd(100) shouldBe "100 is even"
        isEvenOrOdd(99) shouldBe "99 is odd"
    }
})
