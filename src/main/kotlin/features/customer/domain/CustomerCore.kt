package features.customer.domain

import features.customer.domain.ports.CustomerControllerPort
import features.customer.domain.ports.CustomerRepositoryPort
import exceptions.NotFoundException
import org.springframework.stereotype.Service
import pipelineExtensions.orElseThrow
import pipelineExtensions.proceedIf

@Service
class CustomerCore(
    private val customerRepositoryPort: CustomerRepositoryPort,
    private val customerService: CustomerService,
) : CustomerControllerPort {

    override fun getCustomer(id: Long): Customer =
        customerRepositoryPort.getCustomer(id)

    override fun create(customer: Customer): Customer =
        with(customerService) {
            customer.proceedIf { customer -> validateCustomer(customer) }
                .orElseThrow { Exception("Customer with id ${customer.id} is not valid") }
                .let { customer -> customerRepositoryPort.saveCustomer(customer) }
        }

    override fun update(customer: Customer): Customer =
        with(customerService) {
            customer.proceedIf { customer -> validateCustomer(customer) }
                .orElseThrow { Exception("Customer with id ${customer.id} is not valid") }
                .proceedIf { customer -> customerRepositoryPort.existsById(customer) }
                .orElseThrow { NotFoundException("Customer does not exist") }
                .let { customer -> customerRepositoryPort.updateCustomer(customer) }
        }
}
