package de.eventsourcingbook.cart.inventories.internal

import de.eventsourcingbook.cart.common.NoArg
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.repository.CrudRepository
import java.util.UUID

@NoArg
@Entity
data class InventoryProjectionVersion(
    @Id
    @Column(name = "aggregateId")
    var aggregateId: UUID? = null,
    @Column(name = "sequenceNumber")
    var sequenceNumber: Long? = null,
)

interface InventoryProjectionVersionRepository : CrudRepository<InventoryProjectionVersion, UUID>
