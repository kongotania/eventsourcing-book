package de.eventsourcingbook.cart.cartitems.integration

import de.eventsourcingbook.cart.cartitems.CartItemsReadModel
import de.eventsourcingbook.cart.cartitems.CartItemsReadModelQuery
import de.eventsourcingbook.cart.common.support.BaseIntegrationTest
import de.eventsourcingbook.cart.common.support.RandomData
import de.eventsourcingbook.cart.common.support.awaitUntilAssserted
import de.eventsourcingbook.cart.domain.commands.additem.AddItemCommand
import de.eventsourcingbook.cart.domain.commands.clearcart.ClearCartCommand
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/**
Read Model should display an empty list

Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764596814958850
*/
class CartitemswithclearedcartReadModelTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var queryGateway: QueryGateway

    @Test
    fun `Cartitemswithclearedcart Read Model Test`() {
        val aggregateId = RandomData.newInstance<java.util.UUID> {}

        var addItemCommand =
            RandomData.newInstance<AddItemCommand> {
                this.aggregateId = aggregateId
            }

        commandGateway.sendAndWait<Any>(addItemCommand)

        var clearCartCommand =
            RandomData.newInstance<ClearCartCommand> {
                this.aggregateId = aggregateId
            }

        commandGateway.sendAndWait<Any>(clearCartCommand)

        awaitUntilAssserted {
            var readModel = queryGateway.query(CartItemsReadModelQuery(aggregateId, -1), CartItemsReadModel::class.java)
            // TODO add assertions
            assertThat(readModel.get().data).isEmpty()
        }
    }
}
