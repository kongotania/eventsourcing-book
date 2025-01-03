package de.eventsourcingbook.cart.events

import de.eventsourcingbook.cart.common.Event

import java.util.UUID;


/*
Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764595641022548
*/
data class CartClearedEvent(
    var aggregateId:UUID
) : Event
