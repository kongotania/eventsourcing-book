package de.eventsourcingbook.cart.events

import de.eventsourcingbook.cart.common.Event
import org.axonframework.serialization.Revision
import java.util.UUID

@Revision("2")
data class ItemAddedEvent(
    var aggregateId: UUID,
    var description: String,
    var image: String,
    var price: Double,
    var itemId: UUID,
    var productId: UUID,
    // since v2
    var deviceFingerPrint: String,
) : Event
