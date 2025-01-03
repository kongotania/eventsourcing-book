package de.eventsourcingbook.cart.cartitems.integration

import de.eventsourcingbook.cart.cartitems.CartItemsReadModel
import de.eventsourcingbook.cart.cartitems.CartItemsReadModelQuery
import de.eventsourcingbook.cart.common.support.BaseIntegrationTest
import de.eventsourcingbook.cart.common.support.RandomData
import de.eventsourcingbook.cart.common.support.awaitUntilAssserted
import de.eventsourcingbook.cart.domain.commands.additem.AddItemCommand
import de.eventsourcingbook.cart.domain.commands.removeitem.RemoveItemCommand
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/**
Read Model should display an empty list

Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764596814958780
*/
class CartitemswithremoveditemReadModelTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var queryGateway: QueryGateway

    @Test
    fun `Cartitemswithremoveditem Read Model Test`() {
        val aggregateId = RandomData.newInstance<java.util.UUID> {}
        val itemId = UUID.randomUUID()

        var addItemCommand =
            RandomData.newInstance<AddItemCommand> {
                this.aggregateId = aggregateId
                this.itemId = itemId
            }

        commandGateway.sendAndWait<Any>(addItemCommand)

        var removeItemCommand =
            RandomData.newInstance<RemoveItemCommand> {
                this.aggregateId = aggregateId
                this.itemId = itemId
            }

        commandGateway.sendAndWait<Any>(removeItemCommand)

        awaitUntilAssserted {
            var readModel = queryGateway.query(CartItemsReadModelQuery(aggregateId), CartItemsReadModel::class.java)
            // TODO add assertions
            assertThat(readModel.get().data).isEmpty()
        }
    }
}
