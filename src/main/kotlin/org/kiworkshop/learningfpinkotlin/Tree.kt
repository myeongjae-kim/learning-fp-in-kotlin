package org.kiworkshop.learningfpinkotlin

import org.kiworkshop.learningfpinkotlin.Tree.EmptyTree
import org.kiworkshop.learningfpinkotlin.Tree.Node

sealed class Tree<out T : Comparable<@UnsafeVariance T>> {
    object EmptyTree : Tree<Nothing>() {
        override fun toString(): String = "E"
    }

    data class Node<T : Comparable<T>>(val value: T, val left: Tree<T> = EmptyTree, val right: Tree<T> = EmptyTree) :
        Tree<T>() {
        override fun toString(): String = "[${this.left}, ${this.value}, ${this.right}]"
    }

    companion object {
        fun <T : Comparable<T>> empty(): Tree<T> = EmptyTree
    }
}

fun <T : Comparable<T>> Tree<T>.insert(elem: T): Tree<T> = when (this) {
    EmptyTree -> Node(elem)
    is Node -> if (elem < this.value) {
        Node(this.value, this.left.insert(elem), this.right)
    } else {
        Node(this.value, this.left, this.right.insert(elem))
    }
}
