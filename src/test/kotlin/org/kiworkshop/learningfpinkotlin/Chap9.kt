package org.kiworkshop.learningfpinkotlin

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

        booleans.map { x ->
            booleans.map { y ->
                booleans.map { z ->
                    cons.map { c ->
                        validateMonoid(c(), x, y, z)
                    }
                }
            }
        }
    }
})
