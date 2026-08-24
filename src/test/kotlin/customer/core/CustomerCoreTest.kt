package customer.core

import customer.factory.CustomerFactory
import exceptions.NotFoundException
import features.customer.domain.Customer
import features.customer.domain.CustomerCore
import features.customer.domain.CustomerService
import features.customer.domain.ports.CustomerRepositoryPort
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("customer/core")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "customer.core,cucumber")
class CustomerCoreFeature

class CustomerCoreTest {

    private val customerRepositoryPort: CustomerRepositoryPort = mockk()
    private val customerService = CustomerService()
    private val customerCore = CustomerCore(customerRepositoryPort, customerService)

    private var customer: Customer? = null
    private var result: Customer? = null
    private var thrownException: Exception? = null

    @Before
    fun reset() {
        customer = null
        result = null
        thrownException = null
    }

    @Given("a customer exists in the repository with id {long}")
    fun aCustomerExistsInRepositoryWithId(id: Long) {
        customer = CustomerFactory.createCustomer(id = id)
        every { customerRepositoryPort.getCustomer(id) } returns customer!!
    }

    @Given("a core customer with zip code {int}")
    fun aCoreCustomerWithZipCode(zipCode: Int) {
        customer = CustomerFactory.createCustomer(zipCode = zipCode)
    }

    @And("the customer exists in the repository")
    fun theCustomerExistsInRepository() {
        every { customerRepositoryPort.existsById(customer!!) } returns true
        every { customerRepositoryPort.updateCustomer(customer!!) } returns customer!!
    }

    @And("the customer does not exist in the repository")
    fun theCustomerDoesNotExistInRepository() {
        every { customerRepositoryPort.existsById(customer!!) } returns false
    }

    @When("I get the customer with id {long}")
    fun iGetTheCustomerWithId(id: Long) {
        result = customerCore.getCustomer(id)
    }

    @When("the customer is created")
    fun theCustomerIsCreated() {
        every { customerRepositoryPort.saveCustomer(any()) } returns customer!!
        try {
            result = customerCore.create(customer!!)
        } catch (e: Exception) {
            thrownException = e
        }
    }

    @When("the customer is updated")
    fun theCustomerIsUpdated() {
        try {
            result = customerCore.update(customer!!)
        } catch (e: Exception) {
            thrownException = e
        }
    }

    @Then("the customer with id {long} is returned")
    fun theCustomerWithIdIsReturned(id: Long) {
        result!!.id shouldBe id
    }

    @Then("the customer is saved to the repository")
    fun theCustomerIsSavedToTheRepository() {
        result shouldBe customer
        verify { customerRepositoryPort.saveCustomer(customer!!) }
    }

    @Then("the customer is updated in the repository")
    fun theCustomerIsUpdatedInTheRepository() {
        result shouldBe customer
        verify { customerRepositoryPort.updateCustomer(customer!!) }
    }

    @Then("an exception is thrown for customer")
    fun anExceptionIsThrownForCustomer() {
        thrownException shouldNotBe null
    }

    @Then("a NotFoundException is thrown for customer")
    fun aNotFoundExceptionIsThrownForCustomer() {
        thrownException.shouldBeInstanceOf<NotFoundException>()
    }
}
