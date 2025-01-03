package de.eventsourcingbook.cart.common

import java.util.*

data class CommandResult(
    val identifier: UUID,
    val aggregateSequence: Long,
)
