package de.eventsourcingbook.cart.cartitems.integration

import de.eventsourcingbook.cart.cartitems.CartItemsReadModel
import de.eventsourcingbook.cart.cartitems.CartItemsReadModelQuery
import de.eventsourcingbook.cart.common.support.BaseIntegrationTest
import de.eventsourcingbook.cart.common.support.RandomData
import de.eventsourcingbook.cart.common.support.awaitUntilAssserted
import de.eventsourcingbook.cart.domain.commands.additem.AddItemCommand
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/**
Should display one item in the cart

Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764596814958639
*/
class CartitemsReadModelTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var queryGateway: QueryGateway

    @Test
    fun `Cartitems Read Model Test`() {
        val aggregateId = RandomData.newInstance<java.util.UUID> {}

        var addItemCommand =
            RandomData.newInstance<AddItemCommand> {
                this.aggregateId = aggregateId
            }

        commandGateway.sendAndWait<Any>(addItemCommand)

        awaitUntilAssserted {
            var readModel = queryGateway.query(CartItemsReadModelQuery(aggregateId, -1), CartItemsReadModel::class.java)
            // TODO add assertions
            assertThat(readModel.get().data).isNotEmpty
        }
    }
}
