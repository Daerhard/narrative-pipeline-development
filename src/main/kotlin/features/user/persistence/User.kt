package features.user.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import features.user.domain.User as DomainUser

@Table("User")
data class User (

    @Id
    val id: Long,

    @Column
    val firstName: String,

    @Column
    val lastName: String,

    @Column
    var zipCode: Int,

    @Column
    var street: String,

    @Column
    var city: String,

)

fun User.toDomainModel(): DomainUser{
    return DomainUser(
        id = id,
        firstName = firstName,
        lastName = lastName,
        zipCode = zipCode,
        street = street,
        city = city
    )
}

fun DomainUser.toEntity(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        zipCode = zipCode,
        street = street,
        city = city
    )
}