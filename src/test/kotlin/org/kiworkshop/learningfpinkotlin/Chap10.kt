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

    "Example 10-11" {
        (funStreamOf(1, 2) append funStreamOf(3, 4)).toString() shouldBe "[1, 2, 3, 4]"

        funStreamOf(1, 2, 3).foldMap({ funStreamOf(it * it) }, FunStreamMonoid()).toString() shouldBe "[1, 4, 9]"
    }

    "Example 10-12" {
        funStreamOf(1, 2, 3).fmap { it * it }.toString() shouldBe "[1, 4, 9]"
        funStreamOf(1, 2, 3).fmap { println(it); it * it }.take(1).toString() shouldBe "[1]"
    }

    "Example 10-13" {
        FunStream.pure(1).toString() shouldBe "[1]"
        (FunStream.pure { x: Int -> x * x } apply funStreamOf(1, 2, 3)).toString() shouldBe "[1, 4, 9]"
    }

    // foldRight, flatten이 게으르게 작동할 수 있나?
    // 어떤 값으로 매핑이 될 줄 알고 게을러? 문제에서 말하는 게으름이 뭐지
    // 저자 코드도 내 코드랑 거의 비슷한데.
    "Example 10-14" {
        funStreamOf(1, 2, 3).foldRight(0) { curr, acc -> acc + curr } shouldBe 6

        funStreamOf(funStreamOf(1), funStreamOf(2)).flatten().take(1).toString() shouldBe "[1]"

        funStreamOf(1, 2, 3).flatMap { funStreamOf(it * it) }.toString() shouldBe "[1, 4, 9]"

        // 1, 2, 3이 모두 찍힌다. FunStream의 flatMap은 무엇에 대해서 게으른거지?
        (funStreamOf(1, 2, 3).flatMap { println(it); funStreamOf(it * it) } as FunStream<Int>).take(1)
            .toString() shouldBe "[1]"
    }

    "Example 10-14 of authors" {
        fun <T> FunStream<FunStream<T>>.flatten2(): FunStream<T> {
            println("flatten2")
            return when (this) {
                FunStream.Nil -> FunStream.Nil
                is FunStream.Cons -> head() append tail().flatten2()
            }
        }

        fun <T, R> FunStream<T>.foldRight2(acc: R, f: (T, R) -> R): R {
            println("foldRight2")
            return when (this) {
                FunStream.Nil -> acc
                is FunStream.Cons -> f(head(), tail().foldRight2(acc, f))
            }
        }

        infix fun <T, R> FunStream<T>.flatMap2(f: (T) -> FunStream<R>): FunStream<R> {
            println("flatMap2")
            return when (this) {
                FunStream.Nil -> FunStream.Nil
                is FunStream.Cons -> f(head()) append tail().flatMap2(f)
            }
        }

        val valueStream: FunStream<Int> = funStreamOf(1, 2, 3)
        val functionStream: (Int) -> FunStream<Int> = { x ->
            funStreamOf(x, x * 2, x * 3)
        }
        val flatMapResult = valueStream flatMap2 functionStream
        flatMapResult.toString() shouldBe "[1, 2, 3, 2, 4, 6, 3, 6, 9]"

        val funStream: FunStream<FunStream<Int>> = funStreamOf(funStreamOf(1, 2), funStreamOf(3, 4), funStreamOf(5, 6))
        val flattenResult = funStream.flatten2()
        flattenResult.toString() shouldBe "[1, 2, 3, 4, 5, 6]"

        val foldRightStream = funStreamOf(1, 2, 3)
        val foldRightResult = foldRightStream.foldRight2(1) { x, acc -> x * acc }
        foldRightResult shouldBe 6
    }

    // 저자 풀이를 참고했다.
    // 깨달은 것: flatMap은 mappend만 구현하면 만들 수 있다. 바이너리 트리의 mappend는 리스트와는 다르게 left와 right 중에서 선택해야 한다.
    "Example 10-15" {
        FunTree.Node(2, FunTree.Node(1, FunTree.Node(4)), FunTree.Node(3))
            .toString() shouldBe "[[[E, 4, E], 1, E], 2, [E, 3, E]]"

        FunTree.Node(0).mempty() shouldBe FunTree.Nil

        /*
              2
           1    3

            .flatMap { FunTree.Node(it * 10, FunTree.Node(it * 100), FunTree.Node(it * 1000)) }
            shouldBe
                                         20
                              200                   2000
                     10                30
                100     1000      300      3000
         */
        FunTree.Node(2, FunTree.Node(1), FunTree.Node(3))
            .flatMap { FunTree.Node(it * 10, FunTree.Node(it * 100), FunTree.Node(it * 1000)) }
            .toString() shouldBe "[[[[E, 100, E], 10, [E, 1000, E]], 200, [[E, 300, E], 30, [E, 3000, E]]], 20, [E, 2000, E]]"
    }
})
