package de.eventsourcingbook.cart.events

import de.eventsourcingbook.cart.common.Event

import java.util.UUID;


/*
Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764596402400430
*/
data class CartCreatedEvent(
    var aggregateId:UUID
) : Event
