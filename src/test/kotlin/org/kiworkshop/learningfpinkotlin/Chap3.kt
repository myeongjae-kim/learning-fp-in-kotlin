package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveMinLength

class Chap3 : StringSpec({
    "Example 3-1" {
        """
            - 명제: fib(n)은 음이 아닌 정수 n에 대해서 n번째 피보나치 수를 올바르게 계산해서 반환한다.
            - n = 0인 경우, 0을 반환하므로 참이다.
            - n = 1인 경우, 1을 반환하므로 참이다.
            - 임의의 양의 정수 k에 대해서 n < k인 경우, k번쨰 피보나치 수를 올바르게 계산하여 반환한다고 가정한다.
            - n = k 인 경우 fib(k - 1)과 fib(k - 2)를 호출할 때 2번 가정에 의해서 (k - 1), (k - 2) 번째
              피보나치 수를 올바르게 계산한다. 이렇게 나온 두 값을 더하여 반환하므로, fib 함수는 n번째 피보나치 수를
              올바르게 계산한다.
        """ shouldHaveMinLength 0
    }

    "Example 3-2" {
        fun power(x: Double, n: Int): Double = if (n == 0) 1.0 else power(x, n - 1) * x

        power(2.0, 3) shouldBe 8.0
        power(2.0, 10) shouldBe 1024.0
        power(3.0, 3) shouldBe 27.0
    }

    "Example 3-3" {
        fun factorial(n: Int): Int = if (n == 0) 1 else factorial(n - 1) * n

        factorial(0) shouldBe 1
        factorial(1) shouldBe 1
        factorial(2) shouldBe (2 * 1)
        factorial(3) shouldBe (3 * 2 * 1)
        factorial(4) shouldBe (4 * 3 * 2 * 1)
    }

    "Example 3-4" {
        fun toBinary(n: Int): String {
            fun toBinaryRecur(n: Int): String =
                if (n == 0)
                    ""
                else
                    toBinaryRecur(n shr 1) + (n and 1).toString()

            return if (n == 0)
                "0"
            else
                toBinaryRecur(n)
        }

        toBinary(0) shouldBe "0"
        toBinary(1) shouldBe "1"
        toBinary(2) shouldBe "10"
        toBinary(3) shouldBe "11"
        toBinary(1024) shouldBe "10000000000"
        toBinary(1023) shouldBe "1111111111"
    }

    "Example 3-5" {
        fun replicate(n: Int, element: Int): List<Int> = if (n == 0)
            listOf()
        else
            replicate(n - 1, element) + element

        replicate(3, 5) shouldContainExactly listOf(5, 5, 5)
        replicate(2, 10) shouldContainExactly listOf(10, 10)
    }

    "Example 3-6" {
        fun elem(num: Int, list: List<Int>): Boolean = if (list.isEmpty())
            false
        else
            list.head() == num || elem(num, list.tail())

        elem(10, listOf(1, 2, 3, 10)) shouldBe true
        elem(10, listOf(1, 2, 3)) shouldBe false
    }

    "Example 3-7" {
        operator fun <T> Sequence<T>.plus(other: () -> Sequence<T>) = object : Sequence<T> {
            private val thisIterator: Iterator<T> by lazy { this@plus.iterator() }
            private val otherIterator: Iterator<T> by lazy { other().iterator() }

            override fun iterator() = object : Iterator<T> {
                override fun next(): T =
                    if (thisIterator.hasNext())
                        thisIterator.next()
                    else
                        otherIterator.next()

                override fun hasNext(): Boolean = thisIterator.hasNext() || otherIterator.hasNext()
            }
        }

        fun repeat(n: Int): Sequence<Int> = sequenceOf(n) + { repeat(n) }

        /*
        이걸 원한건 아니겠지
        fun takeSequence(n: Int, sequence: Sequence<Int>): List<Int> = sequence.take(5).toList()
         */

        fun takeSequence(n: Int, sequence: Sequence<Int>): List<Int> = sequence.iterator().let {
            var list = listOf<Int>()
            for (i in 1..n) {
                if (!it.hasNext()) {
                    break
                }
                list = list + it.next()
            }

            return list
        }

        takeSequence(5, repeat(3)).toString() shouldBe "[3, 3, 3, 3, 3]"
    }
})
