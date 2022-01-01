package org.kiworkshop.learningfpinkotlin

sealed class FunTree<out A> : Monad<A> {

    companion object;

    override fun <V> pure(value: V): FunTree<V> = FunTree.pure(value)

    object Nil : FunTree<Nothing>() {
        override fun toString(): String = "E"

        override fun <B> flatMap(f: (Nothing) -> Monad<B>): FunTree<B> = Nil
    }

    data class Node<out A>(val value: A, val left: FunTree<A> = Nil, val right: FunTree<A> = Nil) : FunTree<A>() {
        override fun toString(): String {
            return "[$left, $value, $right]"
        }

        // left, right 두 개 중 하나를 선택해야 할 때는 left에 우선을 두도록 한다.
        // append 구현해서 head, left, right 순서로 append 하면 된다.
        override fun <B> flatMap(f: (A) -> Monad<B>): FunTree<B> {
            val v = f(value) as FunTree<B>
            val l = left.flatMap(f) as FunTree<B>
            val r = right.flatMap(f) as FunTree<B>

            return v mappend l mappend r
        }
    }
}

fun <A> FunTree.Companion.pure(value: A) = FunTree.Node(value)

fun <A> FunTree<A>.mempty() = FunTree.Nil
infix fun <A> FunTree<A>.mappend(other: FunTree<A>): FunTree<A> = when (this) {
    FunTree.Nil -> other
    is FunTree.Node -> when (other) {
        FunTree.Nil -> this
        is FunTree.Node -> when (left) {
            FunTree.Nil -> FunTree.Node(value, other, right)
            is FunTree.Node -> when (right) {
                FunTree.Nil -> FunTree.Node(value, left, other)
                is FunTree.Node -> FunTree.Node(value, left.mappend(other), right)
            }
        }
    }
}
