package org.kiworkshop.learningfpinkotlin

import org.kiworkshop.learningfpinkotlin.FunList.Cons
import org.kiworkshop.learningfpinkotlin.FunList.Nil

sealed class FunList<out T> {
    object Nil : FunList<Nothing>()
    data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>()
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
