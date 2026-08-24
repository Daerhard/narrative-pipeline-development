package features.customer.domain.ports

import features.customer.domain.Customer

interface CustomerRepositoryPort {
    fun getCustomer(id: Long): Customer
    fun saveCustomer(customer: Customer): Customer
    fun updateCustomer(customer: Customer): Customer
    fun existsById(customer: Customer): Boolean
}
