package features.customer.domain.ports

import features.customer.domain.Customer

interface CustomerControllerPort {
    fun getCustomer(id: Long): Customer
    fun create(customer: Customer): Customer
    fun update(customer: Customer): Customer
}
