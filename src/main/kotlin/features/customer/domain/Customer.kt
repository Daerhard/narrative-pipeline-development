package features.customer.domain

data class Customer(
    val id: Long,
    val name: String,
    val zipCode: Int,
    val street: String,
    val city: String,
)
