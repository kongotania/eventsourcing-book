package de.eventsourcingbook.cart.cartitems.internal

import de.eventsourcingbook.cart.cartitems.CartItemsReadModel
import de.eventsourcingbook.cart.cartitems.CartItemsReadModelQuery
import de.eventsourcingbook.cart.support.internal.projection.doOnVersionMatch
import org.axonframework.queryhandling.QueryHandler
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/*
Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764595831018749
*/
@Component
class CartItemsReadModelQueryHandler(
    private val repository: CartItemsReadModelRepository,
    private val projectionVersionRepository: CartItemProjectionVersionRepository,
) {
    @QueryHandler
    fun handleQuery(query: CartItemsReadModelQuery): Mono<CartItemsReadModel>? =
        doOnVersionMatch(
            {
                query.expectedVersion == -1L ||
                    query.expectedVersion <= (
                        projectionVersionRepository.findByIdOrNull(query.aggregateId)?.sequenceNumber
                            ?: -1
                    )
            },
            { CartItemsReadModel(repository.findByAggregateId(query.aggregateId)) },
        )
}
