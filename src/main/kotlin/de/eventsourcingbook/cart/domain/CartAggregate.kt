package de.eventsourcingbook.cart.domain

import de.eventsourcingbook.cart.application.DeviceFingerPrintCalculator
import de.eventsourcingbook.cart.common.CommandException
import de.eventsourcingbook.cart.domain.commands.additem.AddItemCommand
import de.eventsourcingbook.cart.domain.commands.archiveitem.ArchiveItemCommand
import de.eventsourcingbook.cart.domain.commands.clearcart.ClearCartCommand
import de.eventsourcingbook.cart.domain.commands.removeitem.RemoveItemCommand
import de.eventsourcingbook.cart.domain.commands.submitcart.SubmitCartCommand
import de.eventsourcingbook.cart.events.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import java.util.UUID

typealias CartItemId = UUID

typealias ProductId = UUID

@Aggregate
class CartAggregate {
    @AggregateIdentifier var aggregateId: UUID? = null

    val cartItems = mutableMapOf<CartItemId, ProductId>()
    var productPrice = mutableMapOf<ProductId, Double>()
    var submitted = false
    var published = false
    var publicationFailed = false

    // Add Item
    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    fun handle(
        command: AddItemCommand,
        fingerPrintCalculator: DeviceFingerPrintCalculator,
    ) {
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
                itemId = command.itemId,
                deviceFingerPrint = fingerPrintCalculator.calculateDeviceFingerPrint(),
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: CartCreatedEvent) {
        this.aggregateId = event.aggregateId
    }

    @EventSourcingHandler
    fun on(event: ItemAddedEvent) {
        this.cartItems[event.itemId] = event.productId
        this.productPrice[event.productId] = event.price
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
        this.productPrice.remove(cartItems[event.itemId])
    }

    // Clear Cart

    @CommandHandler
    fun handle(command: ClearCartCommand) {
        AggregateLifecycle.apply(CartClearedEvent(command.aggregateId))
    }

    @EventSourcingHandler
    fun on(event: CartClearedEvent) {
        this.cartItems.clear()
        this.productPrice.clear()
    }

    @CommandHandler
    fun handle(command: ArchiveItemCommand) {
        cartItems.entries
            .find { it.value == command.productId }
            ?.let { AggregateLifecycle.apply(ItemArchivedEvent(command.aggregateId, it.key)) }
    }

    @EventSourcingHandler
    fun on(event: ItemArchivedEvent) {
        this.cartItems.remove(event.itemId)
        this.productPrice.remove(cartItems[event.itemId])
    }

    // submit cart
    @CommandHandler
    fun handle(command: SubmitCartCommand) {
        if (cartItems.isEmpty()) {
            throw CommandException("cannot submit empty cart")
        }
        if (submitted) {
            throw CommandException("cannot submit a cart twice")
        }
        AggregateLifecycle.apply(
            CartSubmittedEvent(
                command.aggregateId,
                cartItems.map { OrderedProduct(it.value, productPrice[it.value]!!) },
                cartItems.map { productPrice[it.value]!! }.sumOf { it },
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: CartSubmittedEvent) {
        this.submitted = true
    }

    fun publish() {
        if (!this.submitted) {
            throw CommandException("cannot publish unsubmitted cart")
        }
        if (this.published) {
            throw CommandException("cannot publish cart twice")
        }
        AggregateLifecycle.apply(CartPublishedEvent(this.aggregateId!!))
    }

    @EventSourcingHandler
    fun on(event: CartPublishedEvent) {
        this.published = true
    }

    fun failPublication() {
        AggregateLifecycle.apply(CartPublicationFailedEvent(this.aggregateId!!))
    }

    @EventSourcingHandler
    fun on(event: CartPublicationFailedEvent) {
        this.publicationFailed = true
    }
}
