package org.kiworkshop.learningfpinkotlin

import io.kotest.core.Tuple3
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// 모노이드는 '연산'을 추상화한 것이다. (연산: 식이 나타낸 일정한 규칙에 따라 계산함.)
class Chap9 : StringSpec({

    fun <T> validateMonoid(monoid: Monoid<T>, x: T, y: T, z: T) {
        monoid.run {
            mappend(mempty(), x) shouldBe x
            mappend(x, mempty()) shouldBe x
            mappend(mappend(x, y), z) shouldBe mappend(x, mappend(y, z))
        }
    }

    "Example 9-1 ~ 9-4" {
        val booleans = funListOf(true, false)
        val cons = funListOf(::AnyMonoid, ::AllMonoid)

        booleans.forEach { x ->
            booleans.forEach { y ->
                booleans.forEach { z ->
                    cons.forEach { c ->
                        validateMonoid(c(), x, y, z)
                    }
                }
            }
        }
    }

    "Example 9-5 ~ 9-6" {
        funListOf(
            Tuple3(a = funListOf(true, true, true), b = true, c = true),
            Tuple3(a = funListOf(false, false, false), b = false, c = false),
            Tuple3(a = funListOf(true, false, true), b = true, c = false)
        ).forEach { (input, anyExpected, allExpected) ->
            AnyMonoid().mconcat(input) shouldBe anyExpected
            AllMonoid().mconcat(input) shouldBe allExpected
        }
    }

    "Example 9-7 ~ 9-9" {
        // 처음에 작성한 FunListLiftMonoid. 문제의 의도와 달랐다.
        val list1 = funListOf(1, 2, 3)
        val list2 = funListOf(4, 5)

        val listLiftMonoid = FunListLiftMonoid.monoid(SumMonoid())
        validateMonoid(listLiftMonoid, funListOf(1), funListOf(2), funListOf(3))

        listLiftMonoid.mempty() shouldBe FunList.Nil
        listLiftMonoid.mappend(list1, list2) shouldBe funListOf(5, 6, 6, 7, 7, 8)
        listLiftMonoid.mconcat(funListOf(list1, list2)) shouldBe funListOf(5, 6, 6, 7, 7, 8)

        // 9-9
        listLiftMonoid.mconcat(funListOf(funListOf(1, 5), funListOf(11, 50), funListOf(100)))
            .toString("") shouldBe "[112, 151, 116, 155]"

        // 저자가 의도한 FunListMonoid
        FunListMonoid<Int>().apply {
            mempty() shouldBe FunList.Nil
            mappend(list1, list2) shouldBe funListOf(1, 2, 3, 4, 5)
            mconcat(funListOf(list1, list2)) shouldBe funListOf(1, 2, 3, 5, 4) // foldRight is used

            mconcat(funListOf(funListOf(1, 5), funListOf(11, 50), funListOf(100)))
                .toString("") shouldBe "[1, 5, 11, 50, 100]"
        }
    }

    "Example 9-10" {
        // 리스트를 Foldable 타입 클래스의 인스턴스로 만드는게 아니라 리스트 모노이드를 Foldable 타입 클래스의 인스턴스로 만들라고?
        // 모르겠는데??
        // 저자 코드를 보니 리스트를 Foldable 타입 클래스의 인스턴스로 만드는 것이다.
        funListOf(1, 2, 3).foldMap({ it }, SumMonoid()) shouldBe 6
    }

    "Example 9-11" {
        val tree = Node(1, listOf(Node(2), Node(3)))
        val iterated = mutableListOf<Int>()

        val sum = tree.foldLeft(0) { acc, curr ->
            iterated.add(curr)
            acc + curr
        }

        sum shouldBe 6
        iterated shouldBe mutableListOf(2, 3, 1) // post-order

        tree.foldMap({ it }, m = SumMonoid()) shouldBe 6
    }

    "Example 9-12" {
        funListOf(1, 2, 3).run {
            contains(1) shouldBe true
            contains(4) shouldBe false
        }
    }

    "Example 9-13" {
        Node(1, listOf(Node(2), Node(3))).run {
            contains(1) shouldBe true
            contains(4) shouldBe false
        }
    }

    "Example 9-14" {
        Node(1, listOf(Node(2), Node(3))).toFunList() shouldBe funListOf(2, 3, 1)
    }
})
