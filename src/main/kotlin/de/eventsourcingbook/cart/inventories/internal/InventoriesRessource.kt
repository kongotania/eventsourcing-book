package de.eventsourcingbook.cart.inventories.internal

import de.eventsourcingbook.cart.inventories.InventoriesReadModel
import de.eventsourcingbook.cart.inventories.InventoriesReadModelQuery
import mu.KotlinLogging
import org.axonframework.queryhandling.QueryGateway
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
class InventoriesRessource(
    private var queryGateway: QueryGateway,
    private var versionRepository: InventoryProjectionVersionRepository,
) {
    var logger = KotlinLogging.logger {}

    @CrossOrigin
    @GetMapping("/inventories/{aggregateId}")
    fun findReadModel(
        @PathVariable("aggregateId") aggregateId: UUID,
    ): CompletableFuture<InventoriesReadModel> =
        queryGateway.query(
            InventoriesReadModelQuery(aggregateId),
            InventoriesReadModel::class.java,
        )

    @CrossOrigin
    @GetMapping("/inventories/{aggregateId}/version")
    fun findProjectionVersion(
        @PathVariable("aggregateId") aggregateId: UUID,
    ): InventoryProjectionVersion? = versionRepository.findByIdOrNull(aggregateId)
}
