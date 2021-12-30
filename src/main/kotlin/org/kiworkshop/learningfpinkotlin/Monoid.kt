package org.kiworkshop.learningfpinkotlin

interface Monoid<T> {

    fun mempty(): T

    fun mappend(m1: T, m2: T): T
}

fun <T> Monoid<T>.mconcat(list: FunList<T>): T = list.foldRight(mempty(), ::mappend)

class SumMonoid : Monoid<Int> {
    override fun mempty(): Int = 0

    override fun mappend(m1: Int, m2: Int): Int = m1 + m2
}

class ProductMonoid : Monoid<Int> {
    override fun mempty(): Int = 1

    override fun mappend(m1: Int, m2: Int): Int = m1 * m2
}

class AnyMonoid : Monoid<Boolean> {
    override fun mempty(): Boolean = false

    override fun mappend(m1: Boolean, m2: Boolean): Boolean = m1 || m2
}

class AllMonoid : Monoid<Boolean> {
    override fun mempty(): Boolean = true

    override fun mappend(m1: Boolean, m2: Boolean): Boolean = m1 && m2
}

object ListMonoid {

    fun <T> monoid(inValue: Monoid<T>): Monoid<FunList<T>> = object : Monoid<FunList<T>> {
        override fun mempty(): FunList<T> = FunList.Nil

        override fun mappend(m1: FunList<T>, m2: FunList<T>): FunList<T> = when {
            m1 is FunList.Nil -> m2
            m2 is FunList.Nil -> m1
            m1 is FunList.Cons && m2 is FunList.Cons -> FunList.liftA2 { a: T, b: T -> inValue.mappend(a, b) }(m1, m2)
            else -> FunList.Nil
        }
    }
}
