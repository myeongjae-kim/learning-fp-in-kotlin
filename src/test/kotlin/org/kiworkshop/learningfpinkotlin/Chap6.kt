package org.kiworkshop.learningfpinkotlin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.Tuple3
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kiworkshop.learningfpinkotlin.Tree.EmptyTree
import org.kiworkshop.learningfpinkotlin.Tree.Node

class Chap6 : StringSpec({
    "Example 1" {
        EmptyTree.toString() shouldBe "E"
        Node(1).toString() shouldBe "[E, 1, E]"

        Node(
            1,
            Node(2, Node(4)),
            Node(3, EmptyTree, Node(5))
        ).toString() shouldBe "[[[E, 4, E], 2, E], 1, [E, 3, [E, 5, E]]]"
    }

    listOf(
        Tuple3(
            "complete binary tree",
            listOf(4, 2, 6, 1, 3, 5, 7),
            "[[[E, 1, E], 2, [E, 3, E]], 4, [[E, 5, E], 6, [E, 7, E]]]"
        ),
        Tuple3(
            "skewed binary tree",
            (1..7).toList(),
            "[E, 1, [E, 2, [E, 3, [E, 4, [E, 5, [E, 6, [E, 7, E]]]]]]]"
        )
    ).forEach { (condition, list, expectedString) ->
        "Example 2, $condition" {
            list.fold(Tree.empty<Int>()) { acc, curr -> acc.insert(curr) }.toString() shouldBe expectedString
        }
    }

    "Example 3" {
        shouldThrow<StackOverflowError> {
            (1..100000).fold(Tree.empty<Int>()) { acc, curr -> acc.insert(curr) }
        }
    }
})
