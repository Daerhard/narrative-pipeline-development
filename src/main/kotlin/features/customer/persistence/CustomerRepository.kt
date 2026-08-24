package features.customer.persistence

import features.customer.persistence.Customer
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Long>
