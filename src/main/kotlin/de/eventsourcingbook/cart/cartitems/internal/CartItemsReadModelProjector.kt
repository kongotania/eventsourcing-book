package de.eventsourcingbook.cart.cartitems.internal

import de.eventsourcingbook.cart.cartitems.CartItemsReadModelEntity
import de.eventsourcingbook.cart.cartitems.CartItemsReadModelKey
import de.eventsourcingbook.cart.events.CartClearedEvent
import de.eventsourcingbook.cart.events.CartCreatedEvent
import de.eventsourcingbook.cart.events.ItemAddedEvent
import de.eventsourcingbook.cart.events.ItemArchivedEvent
import de.eventsourcingbook.cart.events.ItemRemovedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.SequenceNumber
import org.axonframework.messaging.MetaData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Component
import java.util.UUID

interface CartItemsReadModelRepository : JpaRepository<CartItemsReadModelEntity, CartItemsReadModelKey> {
    fun findByAggregateId(aggregateId: UUID): List<CartItemsReadModelEntity>

    @Modifying
    fun deleteByAggregateId(aggregateId: UUID)
}

/*
Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764595831018749
*/
@Component
class CartItemsReadModelProjector(
    var repository: CartItemsReadModelRepository,
    var projectionVersionRepository: CartItemProjectionVersionRepository,
) {
    @EventHandler
    fun on(
        event: CartClearedEvent,
        @SequenceNumber sequenceNumber: Long,
    ) {
        Thread.sleep(10000)
        // throws exception if not available (adjust logic)
        repository.deleteByAggregateId(event.aggregateId)
        projectionVersionRepository.save(CartItemsProjectionVersion(event.aggregateId, sequenceNumber))
    }

    @EventHandler
    fun on(
        event: ItemArchivedEvent,
        @SequenceNumber sequenceNumber: Long,
    ) {
        Thread.sleep(10000)
        // throws exception if not available (adjust logic)
        this.repository.deleteById(
            CartItemsReadModelKey(
                event.aggregateId,
                event.itemId,
            ),
        )
        projectionVersionRepository.save(CartItemsProjectionVersion(event.aggregateId, sequenceNumber))
    }

    @EventHandler
    fun on(
        event: CartCreatedEvent,
        @SequenceNumber sequenceNumber: Long,
    ) {
        // throws exception if not available (adjust logic)
        projectionVersionRepository.save(CartItemsProjectionVersion(event.aggregateId, sequenceNumber))
    }

    @EventHandler
    fun on(
        event: ItemAddedEvent,
        metaData: MetaData,
        @SequenceNumber sequenceNumber: Long,
    ) {
        Thread.sleep(10000)
        // throws exception if not available (adjust logic)
        val entity =
            this.repository
                .findById(
                    CartItemsReadModelKey(
                        event.aggregateId,
                        event.itemId,
                    ),
                ).orElse(CartItemsReadModelEntity())
        entity
            .apply {
                aggregateId = event.aggregateId
                description = event.description
                image = event.image
                price = event.price
                totalPrice = event.price
                productId = event.productId
                itemId = event.itemId
            }.also { this.repository.save(it) }
        projectionVersionRepository.save(CartItemsProjectionVersion(event.aggregateId, sequenceNumber))
    }

    @EventHandler
    fun on(
        event: ItemRemovedEvent,
        @SequenceNumber sequenceNumber: Long,
    ) {
        Thread.sleep(10000)
        // throws exception if not available (adjust logic)
        this.repository.deleteById(
            CartItemsReadModelKey(
                event.aggregateId,
                event.itemId,
            ),
        )
        projectionVersionRepository.save(CartItemsProjectionVersion(event.aggregateId, sequenceNumber))
    }
}
