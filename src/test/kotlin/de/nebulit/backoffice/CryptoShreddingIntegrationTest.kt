package de.nebulit.backoffice

import de.nebulit.backoffice.common.support.BaseIntegrationTest
import de.nebulit.backoffice.common.support.RandomData
import de.nebulit.backoffice.common.support.StreamAssertions
import de.nebulit.backoffice.common.support.awaitUntilAssserted
import de.nebulit.backoffice.cryptoshredding.CryptoShreddingSerializer
import de.nebulit.backoffice.cryptoshredding.persistence.CryptoKeyRepository
import de.nebulit.backoffice.domain.commands.assignclerk.AssignClerkCommand
import de.nebulit.backoffice.domain.commands.submitorder.PrepareOrderCommand
import de.nebulit.backoffice.events.OrderPreparedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class CryptoShreddingIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var streamAssertions: StreamAssertions

    @Autowired
    private lateinit var keyRepository: CryptoKeyRepository

    @Test
    fun verifySerialization() {
        val aggregateId = UUID.randomUUID()
        val prepareOrder =
            RandomData.newInstance<PrepareOrderCommand> {
                this.orderId = aggregateId
                this.name = "Martin"
            }

        commandGateway.sendAndWait<AssignClerkCommand>(prepareOrder)

        awaitUntilAssserted {
            streamAssertions.assertEvent(aggregateId.toString()) { event ->
                event is OrderPreparedEvent && event.name == "Martin"
            }
        }
        keyRepository.deleteById(prepareOrder.customerId.toString())
        awaitUntilAssserted {
            streamAssertions.assertEvent(aggregateId.toString()) { event ->
                event is OrderPreparedEvent && event.name == CryptoShreddingSerializer.DELETED_DEFAULT
            }
        }
    }
}
