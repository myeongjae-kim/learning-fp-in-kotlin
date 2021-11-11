package org.kiworkshop.learningfpinkotlin

// list
fun String.head(): Char = first()
fun String.tail(): String = drop(1)

fun <T> List<T>.head(): T = first()
fun <T> List<T>.tail(): List<T> = drop(1)

// trampoline
sealed class Bounce<A>
data class Done<A>(val result: A) : Bounce<A>()
data class More<A>(val thunk: () -> Bounce<A>) : Bounce<A>()

tailrec fun <A> trampoline(bounce: Bounce<A>): A {
    println("trampoline called")

    return when (bounce) {
        is Done -> bounce.result
        is More -> trampoline(bounce.thunk())
    }
}
