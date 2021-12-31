package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kiworkshop.learningfpinkotlin.Maybe.Just

class Chap10 : StringSpec({

    "Example 10-1 ~ 10-2" {
        funListOf(1, 2, 3).run {
            flatMap { funListOf(it * 2) } shouldBe funListOf(2, 4, 6)
            flatMap { Just(it * 2) } shouldBe FunList.Nil

            leadTo(funListOf(4, 5, 6)) shouldBe funListOf(4, 5, 6, 4, 5, 6, 4, 5, 6) // ???????????
            pure(4) shouldBe funListOf(4)
            // apply는 테스트 안해도 되겠는데?
        }
    }

    "Example 10-3" {
        class D4(val value: Maybe<String>)
        class C4(val d: D4)
        class B4(val c: Maybe<C4>)
        class A4(val b: Maybe<B4>)

        fun getValueOfD4(a: A4): Maybe<String> = a.b
            .flatMap { it.c }
            .fmap { it.d }
            .flatMap { it.value }

        val value = "what..?"

        getValueOfD4(A4(Just(B4(Just(C4(D4(Just(value)))))))).toString() shouldBe "Just($value)"
    }

    "Example 10-4" {
        val f = { x: Int -> funListOf(x * 10) }
        (FunList.pure(1) flatMap f) shouldBe funListOf(10)
    }

    "Example 10-5" {
        val pure: (Int) -> FunList<Int> = FunList.Companion::pure
        val m = funListOf(1, 2, 3)

        (m flatMap pure) shouldBe m
    }

    "Example 10-6" {
        val f = { a: Int -> funListOf(a * 2) }
        val g = { a: Int -> funListOf(a + 1) }
        val m = funListOf(10)

        ((m flatMap f) flatMap g) shouldBe (m flatMap { x -> f(x) flatMap g })
        ((m flatMap f) flatMap g) shouldBe funListOf(21)
    }

    "Example 10-7" {
        val f = { a: Int -> funListOf(a * 2) }
        val g = { a: Int -> funListOf(a + 1) }
        val h = { a: Int -> funListOf(a * 10) }
        val pure: (Int) -> FunList<Int> = FunList.Companion::pure

        (pure compose f)(10) shouldBe f(10)
        (f compose pure)(10) shouldBe f(10)
        ((f compose g) compose h)(10) shouldBe (f compose (g compose h))(10)
        ((f compose g) compose h)(10) shouldBe funListOf(((10 * 10 + 1)) * 2)
    }

    "Example 10-8 ~ 10-10" {
        funStreamOf(1, 2, 3).toString() shouldBe "[1, 2, 3]"
    }
})
