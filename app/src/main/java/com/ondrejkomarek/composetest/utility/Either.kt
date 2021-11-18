package com.ondrejkomarek.composetest.utility

sealed class Either<out E, out V> {
	data class Left<out E>(val error: E) : Either<E, Nothing>()
	data class Right<out V>(val value: V) : Either<Nothing, V>()
}

// creators
fun <V> right(value: V): Either<Nothing, V> = Either.Right(value)
fun <E> left(value: E): Either<E, Nothing> = Either.Left(value)

fun <V> either(action: () -> V): Either<Exception, V> = try { right(action()) } catch (e: Exception) { error(e) }

// composing

// functor
inline infix fun <E, V, V2> Either<E, V>.map(func: (V) -> V2): Either<E, V2> = when(this) {
	is Either.Left -> this
	is Either.Right -> Either.Right(func(value))
}

// applicative
fun <F, A, B> Either<F, (A) -> B>.apply(f: Either<F, A>): Either<F, B> = when(this) {
	is Either.Right -> f.map(this.value)
	is Either.Left -> this
}

// chainable - basically chain operator
inline infix fun <E, V, V2> Either<E, V>.flatMap(func: (V) -> Either<E, V2>): Either<E, V2> = when(this) {
	is Either.Left -> this
	is Either.Right -> func(value)
}

inline infix fun <E, E2, V> Either<E, V>.mapError(func: (E) -> E2): Either<E2, V> = when(this) {
	is Either.Left -> Either.Left(func(error))
	is Either.Right -> this
}

// recover from error
inline infix fun <E, V> Either<E, V>.onErrorNext(func: (E) -> Either<E, V>): Either<E, V> = when(this) {
	is Either.Left -> func(error)
	is Either.Right -> this
}

inline fun <E, V, A> Either<E, V>.fold(e: (E) -> A, v: (V) -> A): A = when(this) {
	is Either.Left -> e(this.error)
	is Either.Right -> v(this.value)
}

fun <E, V> Either<E, V>.flip(): Either<V, E> = fold({ right(it) }, { left(it) })
