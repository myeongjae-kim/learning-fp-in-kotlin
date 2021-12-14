package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

fun interface Filter<T> {
    operator fun invoke(iterables: Iterable<T>): List<T>
}

class Chap4 : StringSpec({
    // List<T>.max() 대신 List<T>.maxOrNull()
    // List<T>.min() 대신 List<T>.minOrNull()

    "Example 4-1" {
        // test code 4-16
        val condition: (Int) -> Boolean = { it.rem(2) == 0 }
        val body: (Int) -> String = { "$it is even" }

        val isEven = body.toPartialFunction(condition)

        isEven.isDefinedAt(100).shouldBeTrue()
        isEven(100) shouldBe "100 is even"

        // test invokeOrElse
        val oddNumber = 99
        isEven.invokeOrElse(oddNumber, "$oddNumber is odd") shouldBe "99 is odd"

        // test orElse
        val isEvenOrOdd = isEven.orElse(PartialFunction({ it.rem(2) == 1 }, { "$it is odd" }))
        isEvenOrOdd(100) shouldBe "100 is even"
        isEvenOrOdd(99) shouldBe "99 is odd"
    }

    "Example 4-2" {
        fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partial1(p1: P1): (P2, P3) -> R =
            { p2, p3 -> this(p1, p2, p3) }

        fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partial2(p2: P2): (P1, P3) -> R =
            { p1, p3 -> this(p1, p2, p3) }

        fun <P1, P2, P3, R> ((P1, P2, P3) -> R).partial3(p3: P3): (P1, P2) -> R =
            { p1, p2 -> this(p1, p2, p3) }

        val f: (String, String, String) -> String = { p1, p2, p3 -> "$p1, $p2, $p3" }

        f.partial1("alpha")("beta", "gamma") shouldBe "alpha, beta, gamma"
        f.partial2("alpha")("beta", "gamma") shouldBe "beta, alpha, gamma"
        f.partial3("alpha")("beta", "gamma") shouldBe "beta, gamma, alpha"
    }

    "Example 4-3" {
        fun max(p1: Int): (Int) -> Int = { p2 ->
            kotlin.math.max(p1, p2)
        }

        max(1)(2) shouldBe 2
    }

    "Example 4-4" {
        fun min(p1: Int, p2: Int) = kotlin.math.min(p1, p2)

        val curriedMin: (Int) -> (Int) -> Int = ::min.curried()

        curriedMin(1)(2) shouldBe 1
    }

    fun max(ints: List<Int>): Int? {
        tailrec fun max(ints: List<Int>, acc: Int): Int =
            if (ints.isEmpty())
                acc
            else
                max(ints.tail(), kotlin.math.max(ints.head(), acc))

        return if (ints.isEmpty()) null else max(ints.tail(), ints.head())
    }

    fun power(x: Int?, n: Int): Int? {
        if (x == null) return null

        tailrec fun power(n: Int, acc: Int): Int = when (n) {
            0 -> 1
            1 -> acc
            else -> power(n - 1, acc * x)
        }

        return power(n, x)
    }

    "Example 4-5" {
        max(listOf()) shouldBe null
        max(listOf(1, 2, 3, 4, 5)) shouldBe 5
        power(5, 3) shouldBe 5 * 5 * 5
        power(5, 0) shouldBe 1
        power(null, 50).shouldBeNull()

        fun composed(ints: List<Int>): Int? = power(max(ints), 2)

        composed(listOf(1, 2, 3, 4, 5)) shouldBe 25
        composed(listOf()) shouldBe null
    }

    "Example 4-6" {
        // solution 1
        infix fun <F, G, H, R> ((F, G) -> R).compose(h: (H) -> F): (H, G) -> R =
            { hInput: H, gInput: G -> this(h(hInput), gInput) }

        val composed = ::power compose ::max

        composed(listOf(1, 2, 3, 4, 5), 2) shouldBe 25
        composed(listOf(), 2) shouldBe null

        // solution 2
        val square: (Int?) -> Int? = { power(it, 2) }

        val composed2 = square compose ::max
        composed2(listOf(1, 2, 3, 4, 5)) shouldBe 25
        composed2(listOf()) shouldBe null

        // solution 3
        val getMaxAndPower = ::power.curried() compose ::max
        getMaxAndPower(listOf(1, 2, 3, 4, 5))(2) shouldBe 25
        getMaxAndPower(listOf())(2) shouldBe null
    }

    "만화경 댓글 필터링에 사용한 합성함수" {
        // Filter interface를 구현할 때 생성자에 `next: Filter<T>?`로 매개변수를 받아서 필터를 chaining할 수도 있지만,
        // 필터 자체는 필터링에만 집중하고 chaining은 필터에서 신경쓰지 않도록 구현하고 싶었다. 
        class EvenFilter(private val next: Filter<Int>? = null) : Filter<Int> {
            override fun invoke(iterables: Iterable<Int>): List<Int> {
                val filtered = iterables.filter { it and 1 == 0 }
                return next?.invoke(filtered) ?: filtered
            }
        }

        class MultipleOf3Filter(private val next: Filter<Int>? = null) : Filter<Int> {
            override fun invoke(iterables: Iterable<Int>): List<Int> {
                val filtered = iterables.filter { it % 3 == 0 }
                return next?.invoke(filtered) ?: filtered
            }
        }

        EvenFilter(next = MultipleOf3Filter())(1..17).shouldContainExactly(6, 12)

        // chaining의 책임을 필터 구현체 밖으로 빼내고 싶어서 필터 리스트에 applyFilters라는 확장함수를 구현했다.
        // 개별 필터 구현체에서 담당했던 chaining을 밖으로 빼내니 보일러플레이트 코드가 줄어들어서 만족했다.
        fun <T> List<Filter<T>>.applyFilters(iterables: Iterable<T>): List<T> =
            fold(iterables.toList()) { acc, filter -> filter(acc) }

        val even = Filter<Int> { contents -> contents.filter { it and 1 == 0 } }
        val multipleOf3 = Filter<Int> { contents -> contents.filter { it % 3 == 0 } }

        listOf(even, multipleOf3).applyFilters(1..17).shouldContainExactly(6, 12)

        // 그리고 스터디 준비를 하다가 4장의 합성함수를 발견하고 적용했다..
        infix fun <T> Filter<T>.compose(g: Filter<T>): Filter<T> = Filter { iterables -> this(g(iterables)) }
        (multipleOf3 compose even)(1..17).shouldContainExactly(6, 12)
    }

    "Example 4-7" {
        fun <T> takeWhile(list: List<T>, condition: (T) -> Boolean): List<T> {
            tailrec fun takeWhile(rest: List<T>, acc: List<T>): List<T> = when {
                rest.isEmpty() -> acc
                else -> takeWhile(
                    rest.tail(),
                    acc + rest.head().let { if (condition(it)) listOf(it) else listOf() }
                )
            }

            return takeWhile(list, listOf())
        }

        takeWhile(listOf(1, 2, 3, 4, 5)) { it < 3 }.shouldContainExactly(1, 2)
    }

    "Example 4-8" {
        fun <T> takeWhile(list: Sequence<T>, condition: (T) -> Boolean): List<T> {
            val iterator = list.iterator()
            tailrec fun takeWhile(acc: List<T>): List<T> {
                if (!iterator.hasNext()) {
                    return acc
                }

                val next = iterator.next()
                if (!condition(next)) {
                    return acc
                }

                return takeWhile(acc + next)
            }

            return takeWhile(listOf())
        }

        takeWhile(generateSequence(1) { it + 1 }) { it < 3 }.shouldContainExactly(1, 2)
    }
})
