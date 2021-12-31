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
})
