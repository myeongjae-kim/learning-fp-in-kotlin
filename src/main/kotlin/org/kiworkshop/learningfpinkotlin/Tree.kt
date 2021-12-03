package org.kiworkshop.learningfpinkotlin

sealed class Tree<out T> {
    object EmptyTree : Tree<Nothing>() {
        override fun toString(): String = "EmptyTree"
    }

    data class Node<T>(val value: T, val left: Tree<T> = EmptyTree, val right: Tree<T> = EmptyTree) : Tree<T>() {
        override fun toString(): String = "[${this.left}, ${this.value}, ${this.right}]"
    }
}
