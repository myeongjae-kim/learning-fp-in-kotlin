package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
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

    "Example 5-2" {
        // when
        val doubleList: FunList<Double> = Cons(1.0, Cons(2.0, Cons(3.0, Cons(4.0, Cons(5.0, Nil)))))

        // then
        doubleList.toList().shouldContainExactly(1.0, 2.0, 3.0, 4.0, 5.0)
    }

    "Example 5-3" {
        Cons(1, Nil).addHead(2).toList().shouldContainExactly(2, 1)
        Cons(1, Nil).appendTail(2).toList().shouldContainExactly(1, 2)
        Cons(1, Nil).appendTail(2).reverse().toList().shouldContainExactly(2, 1)
        Cons(1, Nil).addHead(2).addHead(3).getTail().toList().shouldContainExactly(2, 1)
        Cons(1, Nil).addHead(2).addHead(3).getHead() shouldBe 3
    }

    "Example 5-4" {
        // test filter
        val list = Cons(5, Nil)
            .addHead(4)
            .addHead(3)
            .addHead(2)
            .addHead(1)
        list.filter { it < 3 }.toList().shouldContainExactly(1, 2)

        // test drop
        list.toList().shouldContainExactly(1, 2, 3, 4, 5)

        list.drop(3).toList().shouldContainExactly(4, 5)
        list.drop(10).toList().shouldBeEmpty()

        list.toList().shouldContainExactly(1, 2, 3, 4, 5)
    }

    "Example 5-5" {
        val list = Cons(5, Nil)
            .addHead(4)
            .addHead(3)
            .addHead(2)
            .addHead(1)

        list.toList().shouldContainExactly(1, 2, 3, 4, 5)
        list.dropWhile { it != 3 }.toList().shouldContainExactly(3, 4, 5)
        list.toList().shouldContainExactly(1, 2, 3, 4, 5)
    }

    "Example 5-6" {
        val list = Cons(5, Nil)
            .addHead(4)
            .addHead(3)
            .addHead(2)
            .addHead(1)

        list.take(3).toList().shouldContainExactly(1, 2, 3)
    }
})
