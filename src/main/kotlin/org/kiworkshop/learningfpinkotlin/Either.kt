package org.kiworkshop.learningfpinkotlin

sealed class Either<out L, out R> : Functor<R> {
    abstract override fun <R2> fmap(f: (R) -> R2): Either<L, R2>

    companion object
}

data class Left<out L>(val value: L) : Either<L, Nothing>() {

    override fun toString(): String = "Left($value)"

    override fun <R2> fmap(f: (Nothing) -> R2): Either<L, R2> = this
}

data class Right<out R>(val value: R) : Either<Nothing, R>() {

    override fun toString(): String = "Right($value)"

    override fun <R2> fmap(f: (R) -> R2): Either<Nothing, R2> = Right(f(value))
}

fun <A> Either.Companion.pure(value: A) = Right(value)

infix fun <L, A, B> Either<L, (A) -> B>.apply(f: Either<L, A>): Either<L, B> = when (this) {
    is Left -> this
    is Right -> f.fmap(value)
}

fun <L, A, B, R> Either.Companion.liftA2(binaryFunction: (A, B) -> R) =
    { f1: Either<L, A>, f2: Either<L, B> -> Either.pure(binaryFunction.curried()) apply f1 apply f2 }
