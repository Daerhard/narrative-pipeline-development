package features.customer.domain

const val CUSTOMER_ZIP_LIMIT = 100000

class CustomerService {
    fun validateCustomer(customer: Customer): Boolean = customer.zipCode < CUSTOMER_ZIP_LIMIT
}
