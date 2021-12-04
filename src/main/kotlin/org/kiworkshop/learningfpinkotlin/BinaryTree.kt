package org.kiworkshop.learningfpinkotlin

import org.kiworkshop.learningfpinkotlin.BinaryTree.EmptyBinaryTree
import org.kiworkshop.learningfpinkotlin.BinaryTree.BinaryNode

sealed class BinaryTree<out T : Comparable<@UnsafeVariance T>> {
    object EmptyBinaryTree : BinaryTree<Nothing>() {
        override fun toString(): String = "E"
    }

    data class BinaryNode<T : Comparable<T>>(val value: T, val left: BinaryTree<T> = EmptyBinaryTree, val right: BinaryTree<T> = EmptyBinaryTree) :
        BinaryTree<T>() {
        override fun toString(): String = "[${this.left}, ${this.value}, ${this.right}]"
    }

    companion object {
        fun <T : Comparable<T>> empty(): BinaryTree<T> = EmptyBinaryTree
    }
}

fun <T : Comparable<T>> BinaryTree<T>.insert(elem: T): BinaryTree<T> = when (this) {
    EmptyBinaryTree -> BinaryNode(elem)
    is BinaryNode -> if (elem < this.value) {
        BinaryNode(this.value, this.left.insert(elem), this.right)
    } else {
        BinaryNode(this.value, this.left, this.right.insert(elem))
    }
}

enum class Direction {
    LEFT, RIGHT
}

// historyStack의 개수는 log(n)개니까 이정도는 재귀로 돌려도 괜찮겠지.. 라고 생각했지만 skewd tree에서는 history도 O(n)개일 테니까
// 결국 여기서 stackoverflow 발생하겠는데?
fun <T : Comparable<T>> stackToTree(stack: List<Pair<BinaryTree<T>, Direction>>): BinaryTree<T> {
    return when {
        stack.isEmpty() -> EmptyBinaryTree
        stack.size == 1 -> stack.head().first
        else -> {
            val tree = stackToTree(stack.tail())
            val (node, direction) = stack.head()

            return when (tree) {
                EmptyBinaryTree -> EmptyBinaryTree
                is BinaryNode -> {
                    if (direction == Direction.LEFT)
                        BinaryNode(tree.value, node, tree.right)
                    else
                        BinaryNode(tree.value, tree.left, node)
                }
            }
        }
    }
}

tailrec fun <T : Comparable<T>> reverse(
    binaryTree: BinaryTree<T>,
    directions: FunList<Direction>,
    acc: BinaryTree<T> = EmptyBinaryTree
): BinaryTree<T> {
    return when (binaryTree) {
        EmptyBinaryTree -> acc
        is BinaryNode -> when (directions) {
            FunList.Nil -> acc
            is FunList.Cons -> when (directions.head) {
                Direction.LEFT -> reverse(binaryTree.left, directions.tail, BinaryNode(binaryTree.value, acc, binaryTree.right))
                Direction.RIGHT -> reverse(binaryTree.right, directions.tail, BinaryNode(binaryTree.value, binaryTree.left, acc))
            }
        }
    }
}

// 뭘까... 변경하는 부분만 새로 만들면 될 것 같은데.. Tree에서.. 값을 찾는 곳만 새로운 줄기로 만들어서 parent를 교체한다면..
// 그게 가능한가? 그걸 하는게 재귀 호출에서 하는거잖아. tailrec은 어쩌라는거
// 트리를 꼬리재귀 한다는게 무슨말이야?? ?????? ????????????????????????? 꼬리재귀를 해서 값을 넣고 트리를 다시 뒤집으면 될것같은데.. 트리를 뒤집는다는게 무슨 의미지????
// history를 저장해놓고 자리 찾으면 히스토리 거꾸로 꺼내면서 새로운 노드만 붙여주면.. 되겠다.
// list와 tree의 차이는.. 방향이 있다는 것. 어디로 가야할지 길만 정해주면 reverse를 만들 수 있다.
// fun <T : Comparable<T>> reverse(tree: Tree<T>, directions: FunList<Direction>): Tree<T> 구현하기
fun <T : Comparable<T>> BinaryTree<T>.insertTailrec(elem: T): BinaryTree<T> {
    tailrec fun <T : Comparable<T>> BinaryTree<T>.insertTailrec(elem: T, stack: List<BinaryTree<T>>): BinaryTree<T> = when (this) {
        EmptyBinaryTree -> EmptyBinaryTree
        is BinaryNode -> if (elem < this.value) {
            this.left.insertTailrec(elem, listOf(this) + stack)
        } else {
            this.right.insertTailrec(elem, listOf(this) + stack)
        }
    }

    return insertTailrec(elem, listOf())
}

tailrec fun <T : Comparable<T>> BinaryTree<T>.contains(elem: T): Boolean = when (this) {
    EmptyBinaryTree -> false
    is BinaryNode -> when {
        elem < this.value -> this.left.contains(elem)
        elem > this.value -> this.right.contains(elem)
        elem == this.value -> true
        else -> false
    }
}
