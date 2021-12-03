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

// 뭘까... 변경하는 부분만 새로 만들면 될 것 같은데.. Tree에서.. 값을 찾는 곳만 새로운 줄기로 만들어서 parent를 교체한다면..
// 그게 가능한가? 그걸 하는게 재귀 호출에서 하는거잖아. tailrec은 어쩌라는거
// 트리를 꼬리재귀 한다는게 무슨말이야?? ?????? ????????????????????????? 꼬리재귀를 해서 값을 넣고 트리를 다시 뒤집으면 될것같은데.. 트리를 뒤집는다는게 무슨 의미지????
fun <T : Comparable<T>> Tree<T>.insertTailrec(elem: T): Tree<T> {
    tailrec fun <T : Comparable<T>> Tree<T>.insertTailrec(elem: T, parent: Tree<T>): Tree<T> = when (this) {
        EmptyTree -> parent
        is Node -> if (elem < this.value) {
            this.left.insertTailrec(elem, Node(this.value, this.left, this.right))
        } else {
            this.right.insertTailrec(elem, Node(this.value, this.left, this.right))
        }
    }

    return insertTailrec(elem, Tree.empty())
}

tailrec fun <T : Comparable<T>> Tree<T>.contains(elem: T): Boolean = when (this) {
    EmptyTree -> false
    is Node -> when {
        elem < this.value -> this.left.contains(elem)
        elem > this.value -> this.right.contains(elem)
        elem == this.value -> true
        else -> false
    }
}
