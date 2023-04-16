package io.craigmiller160.markettracker.portfolio.domain.client

import arrow.core.Either
import io.craigmiller160.markettracker.portfolio.extensions.TryEither
import io.craigmiller160.markettracker.portfolio.extensions.leftIfNull
import kotlin.reflect.KClass
import kotlin.reflect.cast

data class Row(private val data: Map<String, Any?>) {
  fun <T : Any> getOptional(key: String, type: KClass<T>): TryEither<T?> =
      data[key]?.let { Either.catch { type.cast(it) } } ?: Either.Right(null)
  fun <T : Any> getRequired(key: String, type: KClass<T>): TryEither<Any> =
      data[key]?.let { Either.catch { type.cast(it) } }.leftIfNull("Missing $key column")
}
