package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
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

    "Example 4-3" {
        fun max(p1: Int): (Int) -> Int = { p2 ->
            kotlin.math.max(p1, p2)
        }

        max(1)(2) shouldBe 2
    }

    "Example 4-4" {
        fun <P1, P2, R> ((P1, P2) -> R).curried(): (P1) -> (P2) -> R =
            { p1 -> { p2 -> this(p1, p2) } }

        fun min(p1: Int, p2: Int) = kotlin.math.min(p1, p2)

        val curriedMin: (Int) -> (Int) -> Int = ::min.curried()

        curriedMin(1)(2) shouldBe 1
    }

    fun max(ints: List<Int>): Int? {
        tailrec fun max(ints: List<Int>, acc: Int): Int =
            if (ints.isEmpty())
                acc
            else
                max(ints.tail(), kotlin.math.max(ints.head(), acc))

        return if (ints.isEmpty()) null else max(ints.tail(), ints.head())
    }

    fun power(x: Int?, n: Int): Int? {
        if (x == null) return null

        tailrec fun power(n: Int, acc: Int): Int = when (n) {
            0 -> 1
            1 -> acc
            else -> power(n - 1, acc * x)
        }

        return power(n, x)
    }

    "Example 4-5" {
        max(listOf()) shouldBe null
        max(listOf(1, 2, 3, 4, 5)) shouldBe 5
        power(5, 3) shouldBe 5 * 5 * 5
        power(5, 0) shouldBe 1
        power(null, 50).shouldBeNull()

        fun composed(ints: List<Int>): Int? = power(max(ints), 2)

        composed(listOf(1, 2, 3, 4, 5)) shouldBe 25
        composed(listOf()) shouldBe null
    }
})
