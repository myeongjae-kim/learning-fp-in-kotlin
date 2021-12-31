package org.kiworkshop.learningfpinkotlin

import org.kiworkshop.learningfpinkotlin.FunList.Cons
import org.kiworkshop.learningfpinkotlin.FunList.Nil
import kotlin.math.max

sealed class FunList<out T> : Functor<T>, Foldable<T>, Monad<T> {
    abstract override fun <B> fmap(f: (T) -> B): FunList<B>
    abstract override fun <B> flatMap(f: (T) -> Monad<B>): FunList<B>

    companion object {}

    override fun <V> pure(value: V): FunList<V> = Cons(value, Nil)

    object Nil : FunList<Nothing>() {
        override fun <B> fmap(f: (Nothing) -> B): FunList<B> = Nil
        override fun <B> foldLeft(acc: B, f: (B, Nothing) -> B): B = acc

        override fun <B> flatMap(f: (Nothing) -> Monad<B>): FunList<B> = Nil
    }

    data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>() {
        override fun <B> fmap(f: (T) -> B): FunList<B> {
            tailrec fun fmap(list: FunList<T>, acc: FunList<B>): FunList<B> = when (list) {
                Nil -> acc.reverse()
                is Cons -> fmap(list.tail, acc.addHead(f(list.head)))
            }
            return fmap(this, Nil)
        }

        override fun <B> foldLeft(acc: B, f: (B, T) -> B): B {
            tailrec fun <T, R> FunList<T>.foldLeftTailrec(acc: R, f: (R, T) -> R): R = when (this) {
                Nil -> acc
                is Cons -> tail.foldLeftTailrec(f(acc, head), f)
            }

            return foldLeftTailrec(acc, f)
        }

        override fun <B> flatMap(f: (T) -> Monad<B>): FunList<B> = try {
            fmap(f).foldMap({ it as FunList<B> }, FunListMonoid())
        } catch (e: ClassCastException) {
            Nil
        }

        // 실제로는 매개변수를 ff: FunList<(T) -> B>로 해야하지만 변성 문제때문에 매개변수가 FunList가 아닌 경우 Nil을 내보내도록 한다.
        // 변성 문제를 확장함수로 회피했음.. Applicative interface를 사용하는 대신 pure와 apply 확장함수를 작성했다.
        /*
        override fun <B> apply(ff: Applicative<(T) -> B>): FunList<B> = when (ff) {
            is FunList -> {
                (ff.fmap { this.fmap(it) } as FunList<FunList<B>>)
                    .foldLeft(Nil as FunList<B>) { acc, curr -> acc.concat(curr) }
            }
            else -> Nil
        }
         */
    }
}

fun <A> FunList.Companion.pure(value: A): FunList<A> = Cons(value, Nil)

infix fun <A, B> FunList<(A) -> B>.apply(ff: FunList<A>): FunList<B> =
    this.fmap { ff.fmap(it) }.foldLeft(Nil as FunList<B>) { acc, curr -> acc.append(curr) }

infix fun <T> FunList<T>.append(other: FunList<T>): FunList<T> {
    tailrec fun <T> FunList<T>.concatRecur(other: FunList<T>): FunList<T> {
        return when (this) {
            Nil -> other
            is Cons -> when (other) {
                Nil -> this
                is Cons -> {
                    this.tail.concatRecur(other.addHead(this.head))
                }
            }
        }
    }

    return this.reverse().concatRecur(other)
}

fun <T> funListOf(vararg elements: T): FunList<T> = elements.toFunList()

fun <T> Array<out T>.toFunList(): FunList<T> = when {
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

fun <T> FunList<T>.takeWhile(p: (T) -> Boolean): FunList<T> {
    tailrec fun FunList<T>.takeWhile(acc: FunList<T>): FunList<T> = when (this) {
        Nil -> this
        is Cons -> if (!p(this.head))
            acc.reverse()
        else
            this.tail.takeWhile(acc.addHead(head))
    }

    val result = this.takeWhile(Nil)
    // 모든 값이 함수 p를 만족하지 않는다면 원본 List를 반환
    return if (result == Nil) this else result
}

tailrec fun <T, R> FunList<T>.map(acc: FunList<R> = Nil, f: (T) -> R): FunList<R> = when (this) {
    Nil -> acc.reverse()
    is Cons -> tail.map(acc.addHead(f(head)), f)
}

fun <T> FunList<T>.forEach(f: (T) -> Unit) = map(Nil, f)

tailrec fun <T, R> FunList<T>.indexedMap(index: Int = 0, acc: FunList<R> = Nil, f: (Int, T) -> R): FunList<R> =
    when (this) {
        Nil -> acc.reverse()
        is Cons -> tail.indexedMap(index + 1, acc.addHead(f(index, this.head)), f)
    }

// precondition: 리스트의 모든 값은 0보다 크고, 리스트의 크기는 1보다 크다.
fun FunList<Int>.maximumByFoldLeft(): Int = foldLeft(0) { acc, it -> max(acc, it) }

fun <T> FunList<T>.filterByFoldLeft(p: (T) -> Boolean): FunList<T> =
    foldLeft(Nil as FunList<T>) { acc, it -> if (p(it)) acc.addHead(it) else acc }.reverse()

fun <T, R> FunList<T>.foldRight(acc: R, f: (T, R) -> R): R = when (this) {
    Nil -> acc
    is Cons -> f(head, tail.foldRight(acc, f))
}

// appendTail을 사용했으므로 시간복잡도는 O(n^2)다. 굳이 이렇게..?
fun <T> FunList<T>.reverseByFoldRight(): FunList<T> = foldRight(Nil as FunList<T>) { it, acc -> acc.appendTail(it) }

fun <T> FunList<T>.filterByFoldRight(p: (T) -> Boolean): FunList<T> =
    foldRight(Nil as FunList<T>) { it, acc -> if (p(it)) acc.addHead(it) else acc }

fun <T, R> FunList<T>.zip(other: FunList<R>, acc: FunList<Pair<T, R>> = Nil): FunList<Pair<T, R>> =
    zipWith(::Pair, other, acc)

tailrec fun <T1, T2, R> FunList<T1>.zipWith(
    f: (T1, T2) -> R,
    list: FunList<T2>,
    acc: FunList<R> = Nil
): FunList<R> = when {
    this === Nil || list === Nil -> acc.reverse()
    else -> getTail().zipWith(f, list.getTail(), acc.addHead(f(this.getHead(), list.getHead())))
}

// mapOf() 대신 mutableMap()을 사용하면 하나의 맵 객체를 재활용할 수 있다. 마지막에 `.toMap()`을 호출해서 immutable Map 으로 리턴하면 됨.
fun <T, R> FunList<T>.associate(f: (T) -> Pair<T, R>): Map<T, R> = this.map(Nil, f).foldLeft(mapOf()) { acc, curr ->
    acc + curr
}

fun <T, K> FunList<T>.groupBy(f: (T) -> K): Map<K, FunList<T>> =
    this.map(Nil) { Pair(f(it), it) }
        .foldLeft<MutableMap<K, FunList<T>>>(mutableMapOf()) { acc, (key, value) ->
            acc[key] = (acc[key] ?: funListOf()).addHead(value)
            acc
        }.map { (key, value) -> Pair(key, value.reverse()) }
        .toMap()

private const val DELIMITER = ", "
tailrec fun <T> FunList<T>.toString(acc: String): String = when (this) {
    Nil -> "[${acc.drop(DELIMITER.length)}]"
    is Cons -> this.tail.toString("$acc$DELIMITER${this.head}")
}

fun <T> FunList<T>.toStringByFoldLeft(): String =
    "[${foldLeft("") { acc, curr -> "$acc$DELIMITER$curr" }.drop(DELIMITER.length)}]"

fun <T> FunList<T>.toList(): List<T> {
    tailrec fun FunList<T>.toList(acc: MutableList<T>): MutableList<T> = when (this) {
        is Nil -> acc
        is Cons -> this.tail.toList(acc.add(this.head).let { acc })
    }

    return this.toList(mutableListOf())
}

tailrec fun <A, B> FunList<(A) -> B>.myZipTailrec(other: FunList<A>, acc: FunList<B> = Nil): FunList<B> = when (this) {
    Nil -> acc.reverse()
    is Cons -> when (other) {
        Nil -> acc.reverse()
        is Cons -> this.tail.myZipTailrec(other.tail, acc.addHead(this.head(other.head)))
    }
}

infix fun <A, B> FunList<(A) -> B>.myZip(other: FunList<A>): FunList<B> = when (this) {
    Nil -> Nil
    is Cons -> when (other) {
        Nil -> Nil
        is Cons -> Cons(this.head(other.head), this.tail.myZip(other.tail))
    }
}

fun <A, B, R> FunList.Companion.liftA2(binaryFunction: (A, B) -> R) =
    { f1: FunList<A>, f2: FunList<B> -> FunList.pure(binaryFunction.curried()) apply f1 apply f2 }

fun <T> FunList.Companion.cons() = { x: T, xs: FunList<T> -> Cons(x, xs) }

// 만들긴 했는데.. 이게 무슨 의미가 있는 함수지?
fun <T> FunList.Companion.sequenceA(listOfLists: FunList<FunList<T>>): FunList<FunList<T>> = when (listOfLists) {
    Nil -> funListOf(funListOf())
    is Cons -> FunList.pure(cons<T>().curried()) apply listOfLists.head apply sequenceA(listOfLists.tail)
}

fun <T> FunList.Companion.sequenceAByFoldRight(listOfLists: FunList<FunList<T>>): FunList<FunList<T>> =
    listOfLists.foldRight(FunList.pure(funListOf()), liftA2(cons()))

fun <T> FunList<T>.contains(value: T): Boolean = foldMap({ it == value }, AnyMonoid())
