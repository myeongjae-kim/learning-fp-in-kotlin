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

    "Example 9-7 ~ 9-9" {
        val list1 = funListOf(1, 2, 3)
        val list2 = funListOf(4, 5)

        val listMonoid = ListMonoid.monoid(SumMonoid())
        validateMonoid(listMonoid, funListOf(1), funListOf(2), funListOf(3))

        listMonoid.mempty() shouldBe FunList.Nil
        listMonoid.mappend(list1, list2) shouldBe funListOf(5, 6, 6, 7, 7, 8)
        listMonoid.mconcat(funListOf(list1, list2)) shouldBe funListOf(5, 6, 6, 7, 7, 8)

        // 9-9
        listMonoid.mconcat(funListOf(funListOf(1, 5), funListOf(11, 50), funListOf(100)))
            .toString("") shouldBe "[112, 151, 116, 155]"
    }
})
