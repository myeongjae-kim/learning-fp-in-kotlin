package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Chap8 : StringSpec({
    "Example 1" {
        val listOfPartiallyAppliedFunctions: Functor<(Int) -> Int> =
            funListOf(1, 2, 3, 4).fmap { x: Int -> { y: Int -> x * y } }

        listOfPartiallyAppliedFunctions.fmap { it -> it(5) }.shouldBe(funListOf(5, 10, 15, 20))
        listOfPartiallyAppliedFunctions.fmap { it -> it(0) }.shouldBe(funListOf(0, 0, 0, 0))
        listOfPartiallyAppliedFunctions.fmap { it -> it(10) }.shouldBe(funListOf(10, 20, 30, 40))
    }

    "Example 2 and 3" {
        funListOf(1, 2).append(funListOf(3, 4)) shouldBe funListOf(1, 2, 3, 4)

        funListOf<(Int) -> Int>({ it * it }) apply funListOf(1, 2, 3) shouldBe funListOf(1, 4, 9)
        funListOf<(Int) -> Int>({ it * it }, { it * 10 }).apply(funListOf(1, 2, 3))
            .shouldBe(funListOf(1, 4, 9, 10, 20, 30))

        FunList.pure(10) shouldBe funListOf(10)
    }
})
