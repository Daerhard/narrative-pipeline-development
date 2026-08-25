package customer.core

import customer.factory.CustomerFactory
import exceptions.NotFoundException
import features.customer.domain.CUSTOMER_ZIP_LIMIT
import features.customer.domain.CustomerCore
import features.customer.domain.CustomerService
import features.customer.domain.ports.CustomerRepositoryPort
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CustomerCoreUnitTest {

    private val customerRepositoryPort: CustomerRepositoryPort = mockk()
    private val customerService = CustomerService()
    private val customerCore = CustomerCore(customerRepositoryPort, customerService)

    @Test
    fun `getCustomer returns customer from repository`() {
        val customer = CustomerFactory.createCustomer(id = 1)
        every { customerRepositoryPort.getCustomer(1L) } returns customer

        val result = customerCore.getCustomer(1L)

        result shouldBe customer
    }

    @Test
    fun `create saves and returns valid customer`() {
        val customer = CustomerFactory.createCustomer()
        every { customerRepositoryPort.saveCustomer(customer) } returns customer

        val result = customerCore.create(customer)

        result shouldBe customer
        verify { customerRepositoryPort.saveCustomer(customer) }
    }

    @Test
    fun `create throws exception when zip code is at limit`() {
        val customer = CustomerFactory.createCustomer(zipCode = CUSTOMER_ZIP_LIMIT)

        assertThrows<Exception> { customerCore.create(customer) }
    }

    @Test
    fun `update updates and returns customer when valid and exists`() {
        val customer = CustomerFactory.createCustomer()
        every { customerRepositoryPort.existsById(customer) } returns true
        every { customerRepositoryPort.updateCustomer(customer) } returns customer

        val result = customerCore.update(customer)

        result shouldBe customer
        verify { customerRepositoryPort.updateCustomer(customer) }
    }

    @Test
    fun `update throws exception when zip code is at limit`() {
        val customer = CustomerFactory.createCustomer(zipCode = CUSTOMER_ZIP_LIMIT)

        assertThrows<Exception> { customerCore.update(customer) }
    }

    @Test
    fun `update throws NotFoundException when customer does not exist`() {
        val customer = CustomerFactory.createCustomer()
        every { customerRepositoryPort.existsById(customer) } returns false

        val exception = assertThrows<Exception> { customerCore.update(customer) }

        exception.shouldBeInstanceOf<NotFoundException>()
    }
}
