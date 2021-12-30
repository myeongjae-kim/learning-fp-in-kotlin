package org.kiworkshop.learningfpinkotlin

import io.kotest.core.Tuple3
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Chap9 : StringSpec({

    fun <T> validateMonoid(monoid: Monoid<T>, x: T, y: T, z: T) {
        monoid.run {
            mappend(mempty(), x) shouldBe x
            mappend(x, mempty()) shouldBe x
            mappend(mappend(x, y), z) shouldBe mappend(x, mappend(y, z))
        }
    }

    "Example 9-1 ~ 9-4" {
        val booleans = funListOf(true, false)
        val cons = funListOf(::AnyMonoid, ::AllMonoid)

        booleans.forEach { x ->
            booleans.forEach { y ->
                booleans.forEach { z ->
                    cons.forEach { c ->
                        validateMonoid(c(), x, y, z)
                    }
                }
            }
        }
    }

    "Example 9-5 ~ 9-6" {
        funListOf(
            Tuple3(a = funListOf(true, true, true), b = true, c = true),
            Tuple3(a = funListOf(false, false, false), b = false, c = false),
            Tuple3(a = funListOf(true, false, true), b = true, c = false)
        ).forEach { (input, anyExpected, allExpected) ->
            AnyMonoid().mconcat(input) shouldBe anyExpected
            AllMonoid().mconcat(input) shouldBe allExpected
        }
    }
})
