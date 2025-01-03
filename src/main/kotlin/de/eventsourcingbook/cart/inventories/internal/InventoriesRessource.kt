package de.eventsourcingbook.cart.inventories.internal

import de.eventsourcingbook.cart.inventories.InventoriesReadModel
import de.eventsourcingbook.cart.inventories.InventoriesReadModelQuery
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@RestController
class InventoriesRessource(
    private var queryGateway: QueryGateway,
) {
    var logger = KotlinLogging.logger {}

    private val executorService = Executors.newFixedThreadPool(4)

    @CrossOrigin
    @GetMapping("/inventories/{aggregateId}")
    fun findReadModel(
        @PathVariable("aggregateId") aggregateId: UUID,
    ): CompletableFuture<InventoriesReadModel> =
        queryGateway.query(
            InventoriesReadModelQuery(aggregateId),
            InventoriesReadModel::class.java,
        )

    @GetMapping("/inventories/sse/{aggregateId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamEvents(
        @PathVariable aggregateId: UUID,
        response: HttpServletResponse,
    ): SseEmitter {
        response.characterEncoding = "UTF-8"
        response.contentType = MediaType.TEXT_EVENT_STREAM_VALUE
        val emitter = SseEmitter(Long.MAX_VALUE)

        executorService.submit {
            val subscriptionQuery =
                queryGateway.subscriptionQuery(
                    InventoriesReadModelQuery(aggregateId),
                    ResponseTypes.instanceOf(InventoriesReadModel::class.java),
                    ResponseTypes.instanceOf(InventoriesReadModel::class.java),
                )

            emitter.onError {
                subscriptionQuery.cancel()
            }

            emitter.onTimeout {
                subscriptionQuery.cancel()
            }

            val heartbeatStream =
                Flux
                    .interval(Duration.ofSeconds(5))
                    .map {
                        ServerSentEvent
                            .builder<String>()
                            .event("ping")
                            .data("heartbeat")
                            .build()
                    }

            val combinedStream =
                Flux
                    .merge(subscriptionQuery.initialResult(), subscriptionQuery.updates(), heartbeatStream)
                    .doOnError {
                        subscriptionQuery.cancel()
                    }

            combinedStream.subscribe(
                { event -> emitter.send(event) },
                { error ->
                    emitter.completeWithError(error)
                    subscriptionQuery.cancel()
                },
                { emitter.complete() },
            )
        }

        return emitter
    }
}
