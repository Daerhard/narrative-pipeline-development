package customer.domain

import customer.factory.CustomerFactory
import features.customer.domain.Customer
import features.customer.domain.CUSTOMER_ZIP_LIMIT
import features.customer.domain.CustomerService
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("customer/domain")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "customer.domain,cucumber")
class CustomerValidationFeature

class CustomerValidation {

    private val customerService = CustomerService()
    private var customer: Customer? = null
    private var result: Boolean = false

    @Before
    fun reset() {
        customer = null
        result = false
    }

    @Given("a customer with zip code {int}")
    fun aCustomerWithZipCode(zipCode: Int) {
        customer = CustomerFactory.createCustomer(zipCode = zipCode)
    }

    @When("the customer is validated")
    fun theCustomerIsValidated() {
        result = customerService.validateCustomer(customer!!)
    }

    @Then("the customer should be valid")
    fun theCustomerShouldBeValid() {
        withClue("Customer with zipCode ${customer!!.zipCode} should be valid (valid zip code is smaller than $CUSTOMER_ZIP_LIMIT)") {
            result shouldBe true
        }
    }

    @Then("the customer should be invalid")
    fun theCustomerShouldBeInvalid() {
        withClue("Customer with zipCode ${customer!!.zipCode} should be invalid (invalid zip code is greater than $CUSTOMER_ZIP_LIMIT)") {
            result shouldBe false
        }
    }
}
