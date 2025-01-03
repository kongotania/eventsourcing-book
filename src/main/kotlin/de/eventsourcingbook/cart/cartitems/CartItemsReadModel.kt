package de.eventsourcingbook.cart.cartitems

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.util.UUID

data class CartItemsReadModelQuery(
    var aggregateId: UUID,
    var expectedVersion: Long = -1,
)

@Embeddable
data class CartItemsReadModelKey(
    var aggregateId: UUID,
    var itemId: UUID,
)

@IdClass(CartItemsReadModelKey::class)
@Entity
class CartItemsReadModelEntity {
    @Id
    @Column(name = "aggregateId")
    var aggregateId: UUID? = null

    @Column(name = "description")
    var description: String? = null

    @Column(name = "image")
    var image: String? = null

    @Column(name = "price")
    var price: Double? = null

    @Column(name = "totalPrice")
    var totalPrice: Double? = null

    @Column(name = "productId")
    var productId: UUID? = null

    @Id
    @Column(name = "itemId")
    var itemId: UUID? = null
}

/*
Boardlink: https://miro.com/app/board/uXjVKvTN_NQ=/?moveToWidget=3458764595831018749
*/
data class CartItemsReadModel(
    val data: List<CartItemsReadModelEntity>,
)
