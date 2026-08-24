package features.customer.persistence

import exceptions.NotFoundException
import features.customer.domain.Customer
import features.customer.domain.ports.CustomerRepositoryPort
import org.springframework.stereotype.Component
import pipelineExtensions.logInfo
import pipelineExtensions.orElseThrow

@Component
class CustomerRepositoryAdapter(
    private val customerRepository: CustomerRepository,
) : CustomerRepositoryPort {

    override fun getCustomer(id: Long): Customer =
        id.logInfo { "Get customer by id $id" }
            .run { customerRepository.findById(this).orElse(null) }
            .orElseThrow { NotFoundException("Customer not found") }
            .toDomainModel()

    override fun saveCustomer(customer: Customer): Customer =
        customer.logInfo { "Save customer with id ${customer.id}" }
            .toEntity()
            .let { entity -> customerRepository.save(entity) }
            .toDomainModel()

    override fun updateCustomer(customer: Customer): Customer =
        customer.logInfo { "Update customer with id ${customer.id}" }
            .toEntity()
            .let { entity -> customerRepository.save(entity) }
            .toDomainModel()

    override fun existsById(customer: Customer): Boolean =
        customerRepository.existsById(customer.id)
}
