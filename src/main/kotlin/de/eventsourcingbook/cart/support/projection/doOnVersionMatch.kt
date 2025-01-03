package de.eventsourcingbook.cart.support.projection

import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.concurrent.TimeoutException

fun <T> doOnVersionMatch(
    versionMatch: () -> Boolean,
    query: () -> T & Any,
): Mono<T> =
    Mono
        .defer {
            if (versionMatch()) {
                Mono.just(query()) // Complete successfully when versions match
            } else {
                Mono.error(IllegalStateException("Version does not match"))
            }
        }.retryWhen(Retry.fixedDelay(100, Duration.ofMillis(100))) // Retry every 100ms
        .timeout(Duration.ofSeconds(20)) // Timeout after 10 seconds
        .onErrorMap(TimeoutException::class.java) {
            IllegalStateException("Timeout waiting for version to match", it)
        }
