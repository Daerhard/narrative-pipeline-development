package user.domain

import features.user.domain.User
import features.user.domain.UserService
import features.user.domain.ZIP_LIMIT
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
import user.factory.UserFactory

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("user/domain")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "user.domain,cucumber")
class UserValidationFeature

class UserValidation {

    private val userService = UserService()
    private var user: User? = null
    private var result: Boolean = false

    @Before
    fun reset() {
        user = null
        result = false
    }

    @Given("a user with zip code {int}")
    fun aUserWithZipCode(zipCode: Int) {
        user = UserFactory.createUser(zipCode = zipCode)
    }

    @When("the user is validated")
    fun theUserIsValidated() {
        result = userService.validateUser(user!!)
    }

    @Then("the user should be valid")
    fun theUserShouldBeValid() {
        withClue("User with zipCode ${user!!.zipCode} should be valid (valid zip code is smaller then ${ZIP_LIMIT})") {
            result shouldBe true
        }
    }

    @Then("the user should be invalid")
    fun theUserShouldBeInvalid() {
        withClue("User with zipCode ${user!!.zipCode} should be invalid (invalid zip code is greater then ${ZIP_LIMIT})") {
            result shouldBe false
        }
    }
}
