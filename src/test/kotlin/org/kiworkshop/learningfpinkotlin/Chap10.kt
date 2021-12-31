package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Chap10 : StringSpec({

    "Example 10-1 ~ 10-2" {
        funListOf(1, 2, 3).run {
            flatMap { funListOf(it * 2) } shouldBe funListOf(2, 4, 6)
            flatMap { Maybe.Just(it * 2) } shouldBe FunList.Nil

            leadTo(funListOf(4, 5, 6)) shouldBe funListOf(4, 5, 6, 4, 5, 6, 4, 5, 6) // ???????????
            pure(4) shouldBe funListOf(4)
            // apply는 테스트 안해도 되겠는데?
        }
    }
})
