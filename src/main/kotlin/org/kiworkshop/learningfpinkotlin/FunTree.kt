package org.kiworkshop.learningfpinkotlin

sealed class FunTree<out A> : Foldable<A>, Monad<A> {

    companion object;

    override fun <V> pure(value: V): FunTree<V> = FunTree.pure(value)

    object Nil : FunTree<Nothing>() {
        override fun toString(): String = "E"

        override fun <B> flatMap(f: (Nothing) -> Monad<B>): FunTree<B> = Nil
        override fun <B> foldLeft(acc: B, f: (B, Nothing) -> B): B = acc
    }

    data class Node<out A>(val value: A, val left: FunTree<A> = Nil, val right: FunTree<A> = Nil) : FunTree<A>() {
        override fun toString(): String {
            return "[$left, $value, $right]"
        }

        // left, right 두 개 중 하나를 선택해야 할 때는 left에 우선을 두도록 한다.
        // append 구현해서 head, left, right 순서로 append 하면 된다.
        override fun <B> flatMap(f: (A) -> Monad<B>): FunTree<B> {
            val monoid = FunTreeMonoid<B>()

            val v = f(value) as FunTree<B>
            val l = left.flatMap(f) as FunTree<B>
            val r = right.flatMap(f) as FunTree<B>

            return monoid.mappend(monoid.mappend(v, l), r)
        }

        override fun <B> foldLeft(acc: B, f: (B, A) -> B): B {
            val v = f(acc, value)
            val l = left.foldLeft(v, f)
            return right.foldLeft(l, f)
        }
    }
}

fun <A> FunTree.Companion.pure(value: A) = FunTree.Node(value)
