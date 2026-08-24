package features.user.api

import features.user.domain.User as DomainUser

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val zipCode: Int,
    val street: String,
    val city: String,
)

fun User.toDomainModel(): DomainUser {
    return DomainUser(
        id = id,
        firstName = firstName,
        lastName = lastName,
        zipCode = zipCode,
        street = street,
        city = city
    )
}

fun DomainUser.toApiModel(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        zipCode = zipCode,
        street = street,
        city = city
    )
}