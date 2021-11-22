package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
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

    fun <T> FunStream<T>.toList(): List<T> {
        tailrec fun FunStream<T>.toList(acc: List<T>): List<T> = when (this) {
            is FunStream.Nil -> acc
            is FunStream.Cons -> this.tail().toList(acc + this.head())
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

    "Example 5-13" {
        funListOf(1, 2, 3, 4, 5).zip(funListOf(1.0, 2.0)).toList().mapIndexed { index, pair ->
            index.shouldBeLessThan(2)
            pair.first shouldBe (index + 1)
            pair.second shouldBe (index + 1).toDouble()
        }
    }

    data class Entry(val english: String, val numeric: Int)
    "Example 5-14" {
        funListOf(1, 2, 3, 4, 5).zipWith(::Pair, funListOf(1.0, 2.0)).toList().mapIndexed { index, pair ->
            index.shouldBeLessThan(2)
            pair.first shouldBe (index + 1)
            pair.second shouldBe (index + 1).toDouble()
        }

        funListOf(Entry("One", 1), Entry("Two", 2), Entry("Three", 3))
            .associate { Pair(it, it.numeric) }
            .let { map ->
                map[Entry("One", 1)] shouldBe 1
                map[Entry("Two", 2)] shouldBe 2
                map[Entry("Three", 3)] shouldBe 3
            }
    }

    "Example 5-15" {
        funListOf(
            Entry("Three", 30),
            Entry("One", 1),
            Entry("Two", 20),
            Entry("One", 10),
            Entry("Two", 2),
            Entry("Three", 3)
        )
            .groupBy { it.english }
            .let { map ->
                map.getValue("One").toList().shouldContainExactly(Entry("One", 1), Entry("One", 10))
                map.getValue("Two").toList().shouldContainExactly(Entry("Two", 20), Entry("Two", 2))
                map.getValue("Three").toList().shouldContainExactly(Entry("Three", 30), Entry("Three", 3))
            }
    }

    // list와 sequence의 차이, atomic kotlin에서 horizontal과 vertical로 비교한거 공유하자.
    fun testBigIntList(
        bigIntList: List<Int>,
        assertImperativeWay: (Long) -> Unit,
        assertFunctionalWay: (Long) -> Unit,
        assertRealFunctionalWay: (Long) -> Unit
    ) {
        fun imperativeWay(intList: List<Int>): Int {
            for (value in intList) {
                val doubleValue = value * value
                if (doubleValue < 10) {
                    return doubleValue
                }
            }

            throw NoSuchElementException("There is no value")
        }

        fun functionalWay(intList: List<Int>): Int = intList.map { n -> n * n }.first { n -> n < 10 }
        fun realFunctionalWay(intList: List<Int>): Int = intList.asSequence().map { n -> n * n }.first { n -> n < 10 }

        fun test(f: (List<Int>) -> Int, assert: (Long) -> Unit) {
            System.currentTimeMillis().let { start ->
                f(bigIntList)
                (System.currentTimeMillis() - start).let {
                    println("imperativeWay: $it ms")
                    assert(it)
                }
            }
        }

        test(::imperativeWay, assertImperativeWay)
        test(::functionalWay, assertFunctionalWay)
        test(::realFunctionalWay, assertRealFunctionalWay)
    }

    "Example 5-16" {
        println("increasing list")
        testBigIntList(
            bigIntList = (1..10000000).toList(),
            assertImperativeWay = { millis: Long -> millis.shouldBeLessThanOrEqual(1) },
            assertFunctionalWay = { millis: Long -> millis.shouldBeGreaterThan(100) },
            assertRealFunctionalWay = { millis: Long ->
                millis.shouldBeGreaterThan(0)
                millis.shouldBeLessThan(10)
            },
        )

        println("\ndecreasing list")
        testBigIntList(
            bigIntList = (10000000 downTo 1).toList(),
            assertImperativeWay = { millis: Long -> millis.shouldBeLessThanOrEqual(1) },
            assertFunctionalWay = { millis: Long -> millis.shouldBeGreaterThan(100) }, // 왜 functionalWay만 100ms가 넘지?
            assertRealFunctionalWay = { millis: Long -> millis.shouldBeLessThanOrEqual(1) },
        )
    }

    "Example 5-17" {
        funStreamOf(1, 2, 3).getHead() shouldBe 1
        funStreamOf(1, 2, 3).getTail()
            .toString() shouldBe "Cons(head=() -> T, tail=() -> org.kiworkshop.learningfpinkotlin.FunStream<T>)"

        funStreamOf(1, 2, 3).getTail().toList().shouldContainExactly(2, 3)

        funStreamOf(1, 2, 3).sum() shouldBe 6
        funStreamOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).sum() shouldBe 55
    }
})
