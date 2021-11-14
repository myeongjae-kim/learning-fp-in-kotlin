package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.kiworkshop.learningfpinkotlin.FunList.Cons
import org.kiworkshop.learningfpinkotlin.FunList.Nil

class Chap5 : StringSpec({
    fun <T> FunList<T>.toList(): List<T> {
        tailrec fun FunList<T>.toList(acc: List<T>): List<T> = when (this) {
            is Nil -> acc
            is Cons -> this.tail.toList(acc + this.head)
        }

        return this.toList(listOf())
    }

    "Example 5-1" {
        // when
        val intList: FunList<Int> = Cons(1, Cons(2, Cons(3, Cons(4, Cons(5, Nil)))))

        // then
        intList.toList().shouldContainExactly(1, 2, 3, 4, 5)
    }
})