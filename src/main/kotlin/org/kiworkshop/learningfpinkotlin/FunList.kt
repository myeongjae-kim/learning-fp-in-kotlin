package org.kiworkshop.learningfpinkotlin

import org.kiworkshop.learningfpinkotlin.FunList.Cons
import org.kiworkshop.learningfpinkotlin.FunList.Nil
import kotlin.math.max

sealed class FunList<out T> {
    object Nil : FunList<Nothing>()
    data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>()
}

fun <T> funListOf(vararg elements: T): FunList<T> = elements.toFunList()

private fun <T> Array<out T>.toFunList(): FunList<T> = when {
    this.isEmpty() -> Nil
    else -> Cons(this[0], this.copyOfRange(1, this.size).toFunList())
}

fun <T> FunList<T>.addHead(head: T): FunList<T> = Cons(head, this)

tailrec fun <T> FunList<T>.appendTail(value: T, acc: FunList<T> = Nil): FunList<T> = when (this) {
    Nil -> Cons(value, acc).reverse()
    is Cons -> tail.appendTail(value, acc.addHead(head))
    // 매번 새로운 list를 생성하지 않으면 immutable하지 않게 된다.
    // 마지막 element 뒤에 Cons를 하나 더 붙이면 기존의 list에도 변화가 있는 것이니,
    // immutable하기 위해서는 addHead와 appendTail중 하나는 O(n)이 되어야 한다.
}

tailrec fun <T> FunList<T>.reverse(acc: FunList<T> = Nil): FunList<T> = when (this) {
    Nil -> acc
    is Cons -> tail.reverse(acc.addHead(head))
}

fun <T> FunList<T>.getTail(): FunList<T> = when (this) {
    Nil -> throw NoSuchElementException()
    is Cons -> tail
}

fun <T> FunList<T>.getHead(): T = when (this) {
    Nil -> throw NoSuchElementException()
    is Cons -> head
}

tailrec fun <T> FunList<T>.filter(acc: FunList<T> = Nil, p: (T) -> Boolean): FunList<T> = when (this) {
    Nil -> acc.reverse()
    is Cons -> if (p(head))
        tail.filter(acc.addHead(head), p)
    else
        tail.filter(acc, p)
}

tailrec fun <T> FunList<T>.drop(n: Int): FunList<T> = when (this) {
    Nil -> this
    is Cons ->
        if (n < 1)
            this
        else
            this.tail.drop(n - 1)
}

tailrec fun <T> FunList<T>.dropWhile(p: (T) -> Boolean): FunList<T> = when (this) {
    Nil -> this
    is Cons -> if (!p(this.head))
        this
    else
        this.tail.dropWhile(p)
}

tailrec fun <T> FunList<T>.take(n: Int, acc: FunList<T> = Nil): FunList<T> = when (this) {
    Nil -> this
    is Cons -> if (n < 1) acc.reverse() else this.tail.take(n - 1, acc.addHead(this.head))
}

tailrec fun <T> FunList<T>.takeWhile(acc: FunList<T> = Nil, p: (T) -> Boolean): FunList<T> = when (this) {
    Nil -> this
    is Cons -> if (!p(this.head))
        acc.reverse()
    else
        this.tail.takeWhile(acc.addHead(head), p)
}

tailrec fun <T, R> FunList<T>.map(acc: FunList<R> = Nil, f: (T) -> R): FunList<R> = when (this) {
    Nil -> acc.reverse()
    is Cons -> tail.map(acc.addHead(f(head)), f)
}

tailrec fun <T, R> FunList<T>.indexedMap(index: Int = 0, acc: FunList<R> = Nil, f: (Int, T) -> R): FunList<R> =
    when (this) {
        Nil -> acc.reverse()
        is Cons -> tail.indexedMap(index + 1, acc.addHead(f(index, this.head)), f)
    }

tailrec fun <T, R> FunList<T>.foldLeft(acc: R, f: (R, T) -> R): R = when (this) {
    Nil -> acc
    is Cons -> tail.foldLeft(f(acc, head), f)
}

// precondition: 리스트의 모든 값은 0보다 크고, 리스트의 크기는 1보다 크다.
fun FunList<Int>.maximumByFoldLeft(): Int = foldLeft(0) { acc, it -> max(acc, it) }

fun <T> FunList<T>.filterByFoldLeft(p: (T) -> Boolean): FunList<T> =
    foldLeft(Nil as FunList<T>) { acc, it -> if (p(it)) acc.addHead(it) else acc }.reverse()
