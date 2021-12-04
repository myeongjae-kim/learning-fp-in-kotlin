package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly

class Chap7 : StringSpec({
    "Example 1" {
        (funListOf(1, 2, 3).fmap { it.toDouble() } as FunList<Double>).toList().shouldContainExactly(1.0, 2.0, 3.0)
    }
})
