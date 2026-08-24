package customer.persistence

import customer.factory.CustomerFactory
import exceptions.NotFoundException
import features.customer.domain.Customer
import features.customer.persistence.CustomerRepository
import features.customer.persistence.CustomerRepositoryAdapter
import features.customer.persistence.toEntity
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import java.util.Optional
import features.customer.persistence.Customer as PersistenceCustomer

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("customer/persistence")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "customer.persistence,cucumber")
class CustomerRepositoryAdapterFeature

class CustomerRepositoryAdapterTest {

    private val customerRepository: CustomerRepository = mockk()
    private val adapter = CustomerRepositoryAdapter(customerRepository)

    private var domainCustomer: Customer? = null
    private var persistenceCustomer: PersistenceCustomer? = null
    private var result: Customer? = null
    private var existsResult: Boolean? = null
    private var thrownException: Exception? = null

    @Before
    fun reset() {
        domainCustomer = null
        persistenceCustomer = null
        result = null
        existsResult = null
        thrownException = null
    }

    @Given("the database contains a customer with id {long}")
    fun theDatabaseContainsACustomerWithId(id: Long) {
        domainCustomer = CustomerFactory.createCustomer(id = id)
        persistenceCustomer = domainCustomer!!.toEntity()
        every { customerRepository.findById(id) } returns Optional.of(persistenceCustomer!!)
        every { customerRepository.existsById(id) } returns true
    }

    @Given("the database does not contain a customer with id {long}")
    fun theDatabaseDoesNotContainACustomerWithId(id: Long) {
        domainCustomer = CustomerFactory.createCustomer(id = id)
        every { customerRepository.findById(id) } returns Optional.empty()
        every { customerRepository.existsById(id) } returns false
    }

    @Given("a domain customer to persist")
    fun aDomainCustomerToPersist() {
        domainCustomer = CustomerFactory.createCustomer()
        persistenceCustomer = domainCustomer!!.toEntity()
        every { customerRepository.save(any()) } returns persistenceCustomer!!
    }

    @When("I fetch the customer with id {long}")
    fun iFetchTheCustomerWithId(id: Long) {
        try {
            result = adapter.getCustomer(id)
        } catch (e: Exception) {
            thrownException = e
        }
    }

    @When("the customer is saved")
    fun theCustomerIsSaved() {
        result = adapter.saveCustomer(domainCustomer!!)
    }

    @When("the adapter updates the customer")
    fun theAdapterUpdatesTheCustomer() {
        result = adapter.updateCustomer(domainCustomer!!)
    }

    @When("I check if the customer with id {long} exists")
    fun iCheckIfTheCustomerWithIdExists(id: Long) {
        existsResult = adapter.existsById(domainCustomer!!)
    }

    @Then("the domain customer with id {long} is returned")
    fun theDomainCustomerWithIdIsReturned(id: Long) {
        result!!.id shouldBe id
    }

    @Then("the repository throws a NotFoundException for customer")
    fun theRepositoryThrowsANotFoundExceptionForCustomer() {
        thrownException.shouldBeInstanceOf<NotFoundException>()
    }

    @Then("the repository saved the entity and the domain customer is returned")
    fun theRepositorySavedTheEntityAndDomainCustomerIsReturned() {
        result shouldBe domainCustomer
        verify { customerRepository.save(any()) }
    }

    @Then("the customer existence check returns true")
    fun theCustomerExistenceCheckReturnsTrue() {
        existsResult shouldBe true
    }

    @Then("the customer existence check returns false")
    fun theCustomerExistenceCheckReturnsFalse() {
        existsResult shouldBe false
    }
}
