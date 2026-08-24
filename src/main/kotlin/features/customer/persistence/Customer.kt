package features.customer.persistence

import features.customer.domain.Customer as DomainCustomer
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("Customer")
data class Customer(
    @Id val id: Long,
    @Column val name: String,
    @Column val zipCode: Int,
    @Column val street: String,
    @Column val city: String,
)

fun Customer.toDomainModel(): DomainCustomer =
    DomainCustomer(
        id = id,
        name = name,
        zipCode = zipCode,
        street = street,
        city = city,
    )

fun DomainCustomer.toEntity(): Customer =
    Customer(
        id = id,
        name = name,
        zipCode = zipCode,
        street = street,
        city = city,
    )