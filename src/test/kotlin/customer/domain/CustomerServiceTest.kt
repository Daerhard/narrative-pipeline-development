package customer.domain

import customer.factory.CustomerFactory
import features.customer.domain.CUSTOMER_ZIP_LIMIT
import features.customer.domain.CustomerService
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CustomerServiceTest {

    private val customerService = CustomerService()

    @Test
    fun `validateCustomer returns true when zip code is below limit`() {
        val customer = CustomerFactory.createCustomer(zipCode = CUSTOMER_ZIP_LIMIT - 1)

        customerService.validateCustomer(customer) shouldBe true
    }

    @Test
    fun `validateCustomer returns false when zip code equals limit`() {
        val customer = CustomerFactory.createCustomer(zipCode = CUSTOMER_ZIP_LIMIT)

        customerService.validateCustomer(customer) shouldBe false
    }

    @Test
    fun `validateCustomer returns false when zip code exceeds limit`() {
        val customer = CustomerFactory.createCustomer(zipCode = CUSTOMER_ZIP_LIMIT + 1)

        customerService.validateCustomer(customer) shouldBe false
    }
}
