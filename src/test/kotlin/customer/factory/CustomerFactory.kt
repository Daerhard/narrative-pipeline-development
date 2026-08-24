package customer.factory

import features.customer.domain.Customer

class CustomerFactory {

    companion object {

        fun createCustomer(
            id: Long = 1,
            name: String = "John Doe",
            zipCode: Int = 86920,
            street: String = "Main Street 1",
            city: String = "Berlin"
        ): Customer = Customer(
            id,
            name,
            zipCode,
            street,
            city
        )
    }
}
