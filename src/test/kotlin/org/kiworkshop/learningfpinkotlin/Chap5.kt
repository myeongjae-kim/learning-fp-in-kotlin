package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.kiworkshop.learningfpinkotlin.FunList.Cons
import org.kiworkshop.learningfpinkotlin.FunList.Nil

class Chap5 : StringSpec({
    // 연습문제 5-7, takeWhile에서 element가 모두 p를 만족하지 못했을 때 원본 리스트가 아니라 빈 리스트를 내보내야 하는거 아닌가?

    val list = funListOf(1, 2, 3, 4, 5)

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
        list.filter { it < 3 }.toList().shouldContainExactly(1, 2)

        // test drop
        list.toList().shouldContainExactly(1, 2, 3, 4, 5)

        list.drop(3).toList().shouldContainExactly(4, 5)
        list.drop(10).toList().shouldBeEmpty()

        list.toList().shouldContainExactly(1, 2, 3, 4, 5)
    }

    "Example 5-5" {
        list.toList().shouldContainExactly(1, 2, 3, 4, 5)
        list.dropWhile { it != 3 }.toList().shouldContainExactly(3, 4, 5)
        list.toList().shouldContainExactly(1, 2, 3, 4, 5)
    }

    "Example 5-6" {
        list.take(3).toList().shouldContainExactly(1, 2, 3)
    }

    "Example 5-7" {
        list.takeWhile { it < 3 }.toList().shouldContainExactly(1, 2)
        list.takeWhile { false }.toList().shouldBeEmpty()

        listOf(1, 2, 3, 4, 5).takeWhile { false }.shouldBeEmpty()
    }

    "Example 5-8" {
        list.map { it * 2 }.toList().shouldContainExactly(2, 4, 6, 8, 10)
        list.indexedMap { i, elem -> (i + 1) * 10 + elem }.toList().shouldContainExactly(11, 22, 33, 44, 55)
    }

    "Example 5-9" {
        list.maximumByFoldLeft() shouldBe 5
    }

    "Example 5-10" {
        list.filterByFoldLeft { it and 1 == 1 }.toList().shouldContainExactly(1, 3, 5)
    }

    "Example 5-11" {
        list.reverseByFoldRight().toList().shouldContainExactly(5, 4, 3, 2, 1)
    }

    "Example 5-12" {
        list.filterByFoldRight { it and 1 == 1 }.toList().shouldContainExactly(1, 3, 5)
    }
})
