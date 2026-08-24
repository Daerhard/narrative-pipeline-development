package features.user.domain

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val zipCode: Int,
    val street: String,
    val city: String,
)
