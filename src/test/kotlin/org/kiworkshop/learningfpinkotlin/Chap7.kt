package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class Chap7 : StringSpec({
    "Example 1" {
        (funListOf(1, 2, 3).fmap { it.toDouble() } as FunList<Double>).toList().shouldContainExactly(1.0, 2.0, 3.0)
    }

    "Example 2" {
        fun <T> identity(t: T) = t

        // 펑터 제 1법칙
        val list = funListOf(1, 2, 3)
        funListOf(1, 2, 3).fmap(::identity).shouldBe(list)

        // 펑터 제 2법칙
        val f: (Int) -> Int = { it + 10 }
        val g: (Int) -> Int = { it * 10 }
        funListOf(1, 2, 3).fmap(f compose g) shouldBe funListOf(1, 2, 3).fmap(g).fmap(f)
        (funListOf(1, 2, 3).fmap(f compose g) as FunList<Int>).toString("") shouldBe "[20, 30, 40]"
    }
})
