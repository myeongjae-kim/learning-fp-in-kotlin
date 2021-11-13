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

    "Example 4-2" {
        fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partial1(p1: P1): (P2, P3) -> R =
            { p2, p3 -> this(p1, p2, p3) }

        fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partial2(p2: P2): (P1, P3) -> R =
            { p1, p3 -> this(p1, p2, p3) }

        fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partial3(p3: P3): (P1, P2) -> R =
            { p1, p2 -> this(p1, p2, p3) }

        val f: (String, String, String) -> String = { p1, p2, p3 -> "$p1, $p2, $p3" }

        f.partial1("alpha")("beta", "gamma") shouldBe "alpha, beta, gamma"
        f.partial2("alpha")("beta", "gamma") shouldBe "beta, alpha, gamma"
        f.partial3("alpha")("beta", "gamma") shouldBe "beta, gamma, alpha"
    }
})
