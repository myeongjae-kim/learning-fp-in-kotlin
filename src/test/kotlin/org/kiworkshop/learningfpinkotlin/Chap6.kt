package org.kiworkshop.learningfpinkotlin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.kiworkshop.learningfpinkotlin.Tree.EmptyTree
import org.kiworkshop.learningfpinkotlin.Tree.Node

class Chap6 : StringSpec({
    "Example 1" {
        EmptyTree.toString() shouldBe "EmptyTree"
        Node(1).toString() shouldBe "[EmptyTree, 1, EmptyTree]"

        Node(
            1,
            Node(2, Node(4)),
            Node(3, EmptyTree, Node(5))
        ).toString() shouldBe "[[[EmptyTree, 4, EmptyTree], 2, EmptyTree], 1, [EmptyTree, 3, [EmptyTree, 5, EmptyTree]]]"
    }
})
