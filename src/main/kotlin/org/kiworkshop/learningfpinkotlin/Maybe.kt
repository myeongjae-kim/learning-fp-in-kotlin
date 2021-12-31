package org.kiworkshop.learningfpinkotlin

sealed class Maybe<out A> : Monad<A> {
    data class Just<out A>(val value: A) : Maybe<A>() {
        override fun toString(): String = "Just($value)"
    }

    object Nothing : Maybe<kotlin.Nothing>() {
        override fun toString(): String = "Nothing"
    }

    companion object {
        fun <V> pure(value: V): Maybe<V> = Just(0).pure(value)
    }

    override fun <V> pure(value: V): Maybe<V> = Just(value)

    override fun <B> fmap(f: (A) -> B): Maybe<B> = super.fmap(f) as Maybe<B>

    override infix fun <B> flatMap(f: (A) -> Monad<B>): Maybe<B> = when (this) {
        is Just -> try {
            f(value) as Maybe<B>
        } catch (e: ClassCastException) {
            Nothing
        }
        Nothing -> Nothing
    }
}
