package org.kiworkshop.learningfpinkotlin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.Tuple3
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.kiworkshop.learningfpinkotlin.Tree.EmptyTree
import org.kiworkshop.learningfpinkotlin.Tree.Node

class Chap6 : StringSpec({
    fun <T : Comparable<T>> List<T>.toTree(): Tree<T> = fold(Tree.empty()) { acc, curr -> acc.insert(curr) }

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
            list.toTree().toString() shouldBe expectedString
        }
    }

    "Example 3" {
        shouldThrow<StackOverflowError> {
            (1..100000).toList().toTree()
        }
    }

    "testStackToTree" {
        stackToTree<Int>(listOf()) shouldBe EmptyTree
        stackToTree(listOf(Pair(Node(1), Direction.LEFT))).toString() shouldBe "[E, 1, E]"
        stackToTree(
            listOf(
                Pair(Node(1), Direction.LEFT),
                Pair(Node(2), Direction.LEFT)
            )
        ).toString() shouldBe "[[E, 1, E], 2, E]"
        stackToTree(
            listOf(
                Pair(Node(1), Direction.LEFT),
                Pair(Node(2, EmptyTree, Node(3)), Direction.LEFT)
            )
        ).toString() shouldBe "[[E, 1, E], 2, [E, 3, E]]"
        stackToTree(
            listOf(
                Pair(Node(1), Direction.RIGHT),
                Pair(Node(2, EmptyTree, Node(3)), Direction.LEFT)
            )
        ).toString() shouldBe "[E, 2, [E, 1, E]]"
    }

    "!reverse" {
        val list = listOf(1, 2, 3, 4).toTree()
        list.toString() shouldBe "[E, 1, [E, 2, [E, 3, [E, 4, E]]]]"
        reverse(list, funListOf(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT)).toString()
            .shouldBe("[E, 4, [E, 3, [E, 2, [E, 1, E]]]]")
        reverse(list, funListOf(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT)).toString()
            .shouldBe("[E, 3, [E, 2, [E, 1, E]]]")
        reverse(list, funListOf(Direction.RIGHT, Direction.RIGHT)).toString()
            .shouldBe("[E, 2, [E, 1, E]]")
        reverse(EmptyTree, funListOf()) shouldBe EmptyTree

        val completeTree = listOf(4, 2, 6, 1, 3, 5, 7).toTree()
        completeTree.toString() shouldBe "[[[E, 1, E], 2, [E, 3, E]], 4, [[E, 5, E], 6, [E, 7, E]]]"
        reverse(completeTree, funListOf(Direction.LEFT, Direction.LEFT, Direction.LEFT)).toString()
            .shouldBe("[[[E, 4, E], 2, [E, 3, E]], 1, [[E, 5, E], 6, [E, 7, E]]]")
        reverse(
            reverse(completeTree, funListOf(Direction.LEFT, Direction.LEFT, Direction.LEFT)),
            funListOf(Direction.LEFT, Direction.LEFT, Direction.LEFT)
        )
            .toString()
            .shouldBe("[[[E, 1, E], 2, [E, 3, E]], 4, [[E, 5, E], 6, [E, 7, E]]]")
        reverse(completeTree, funListOf(Direction.LEFT, Direction.RIGHT)).toString()
            .shouldBe("[[[E, 1, E], 2, [E, 4, E]], 3, [[E, 5, E], 6, [E, 7, E]]]")
    }

    "!Example 4" {
        val tree = listOf(4, 2, 6, 1, 3, 5, 7).toTree()
        tree.insertTailrec(8).toString() shouldBe ""
    }

    "Example 5" {
        val ints = listOf(4, 2, 6, 1, 3, 5, 7)
        val tree = ints.toTree()

        ints.forEach {
            tree.contains(it).shouldBeTrue()
        }

        listOf(0, 8, 9, 1100).forEach {
            tree.contains(it).shouldBeFalse()
        }
    }
})
