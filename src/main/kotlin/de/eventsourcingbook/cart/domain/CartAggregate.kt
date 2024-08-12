package de.eventsourcingbook.cart.domain

import de.eventsourcingbook.cart.common.CommandException
import de.eventsourcingbook.cart.domain.commands.additem.AddItemCommand
import de.eventsourcingbook.cart.domain.commands.clearcart.ClearCartCommand
import de.eventsourcingbook.cart.domain.commands.removeitem.RemoveItemCommand
import de.eventsourcingbook.cart.events.CartClearedEvent
import de.eventsourcingbook.cart.events.CartCreatedEvent
import de.eventsourcingbook.cart.events.ItemAddedEvent
import de.eventsourcingbook.cart.events.ItemRemovedEvent
import java.util.UUID
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class CartAggregate {

  @AggregateIdentifier var aggregateId: UUID? = null

  val cartItems = mutableListOf<UUID>()

  // Add Item
  @CommandHandler
  @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
  fun handle(command: AddItemCommand) {
    if (aggregateId == null) {
      AggregateLifecycle.apply(CartCreatedEvent(aggregateId = command.aggregateId))
    }
    if (cartItems.size >= 3) {
      throw CommandException("can only add 3 items")
    }
    AggregateLifecycle.apply(
        ItemAddedEvent(
            aggregateId = command.aggregateId,
            description = command.description,
            image = command.image,
            price = command.price,
            productId = command.productId,
            itemId = command.itemId))
  }

  @EventSourcingHandler
  fun on(event: CartCreatedEvent) {
    this.aggregateId = event.aggregateId
  }

  @EventSourcingHandler
  fun on(event: ItemAddedEvent) {
    this.cartItems.add(event.itemId)
  }

  // Remove Item

  @CommandHandler
  fun handle(command: RemoveItemCommand) {
    if (!this.cartItems.contains(command.itemId)) {
      throw CommandException("Item ${command.itemId} not in the Cart")
    }
    AggregateLifecycle.apply(ItemRemovedEvent(command.aggregateId, command.itemId))
  }

  @EventSourcingHandler
  fun on(event: ItemRemovedEvent) {
    this.cartItems.remove(event.itemId)
  }

  // Clear Cart

  @CommandHandler
  fun handle(command: ClearCartCommand) {
    AggregateLifecycle.apply(CartClearedEvent(command.aggregateId))
  }

  @EventSourcingHandler
  fun on(event: CartClearedEvent) {
    this.cartItems.clear()
  }
}
