package customer.api

import customer.factory.CustomerFactory
import features.customer.api.CustomerController
import features.customer.domain.Customer
import features.customer.domain.ports.CustomerControllerPort
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest(controllers = [CustomerController::class])
@Import(CustomerControllerTest.MockPortConfiguration::class)
class CustomerControllerTest {

    @TestConfiguration
    class MockPortConfiguration {
        val port: CustomerControllerPort = mockk()

        @Bean
        fun customerControllerPort(): CustomerControllerPort = port
    }

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var mockPortConfiguration: MockPortConfiguration

    @BeforeEach
    fun setUp() {
        clearMocks(mockPortConfiguration.port)
    }

    @Test
    fun `GET customer returns 200 with customer body when found`() {
        val customer = CustomerFactory.createCustomer(id = 1)
        every { mockPortConfiguration.port.getCustomer(1L) } returns customer

        webTestClient.get()
            .uri("/features/customer?customerId=1")
            .exchange()
            .expectStatus().isOk
            .expectBody<Customer>()
            .value { it.id shouldBe 1L }
    }

    @Test
    fun `POST customer returns 201 with created customer`() {
        val customer = CustomerFactory.createCustomer()
        every { mockPortConfiguration.port.create(any()) } returns customer

        webTestClient.post()
            .uri("/features/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customer)
            .exchange()
            .expectStatus().isCreated
            .expectBody<Customer>()
            .value { it.id shouldBe customer.id }
    }

    @Test
    fun `PUT customer returns 200 with updated customer`() {
        val customer = CustomerFactory.createCustomer()
        every { mockPortConfiguration.port.update(any()) } returns customer

        webTestClient.put()
            .uri("/features/customer/${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(customer)
            .exchange()
            .expectStatus().isOk
            .expectBody<Customer>()
            .value { it.id shouldBe customer.id }
    }

    @Test
    fun `POST customer returns 400 for a malformed request body`() {
        webTestClient.post()
            .uri("/features/customer")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isBadRequest
    }
}
