package user.core

import exceptions.NotFoundException
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import features.user.domain.User
import features.user.domain.UserCore
import features.user.domain.UserService
import features.user.domain.ports.UserRepositoryPort
import user.factory.UserFactory

private const val FEATURE_FILE_PATH = "user/domain"
private const val GLUE_CONFIG = "user.core,cucumber"

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource(FEATURE_FILE_PATH)
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = GLUE_CONFIG)
class UserCoreFeature

class UserCoreTest {

    private val userRepositoryPort: UserRepositoryPort = mockk()
    private val userService = UserService()
    private val userCore = UserCore(userRepositoryPort, userService)

    private var user: User? = null
    private var result: User? = null
    private var thrownException: Exception? = null

    @Before
    fun reset() {
        user = null
        result = null
        thrownException = null
    }

    @Given("a user exists in the repository with id {long}")
    fun aUserExistsInRepositoryWithId(id: Long) {
        user = UserFactory.createUser(id = id)
        coEvery { userRepositoryPort.getUser(id) } returns user!!
    }

    @Given("a core user with zip code {int}")
    fun aCoreUserWithZipCode(zipCode: Int) {
        user = UserFactory.createUser(zipCode = zipCode)
    }

    @And("the user exists in the repository")
    fun theUserExistsInRepository() {
        coEvery { userRepositoryPort.existsById(user!!) } returns true
        coEvery { userRepositoryPort.updateUser(user!!) } returns user!!
    }

    @And("the user does not exist in the repository")
    fun theUserDoesNotExistInRepository() {
        coEvery { userRepositoryPort.existsById(user!!) } returns false
    }

    @When("I get the user with id {long}")
    fun iGetTheUserWithId(id: Long) = runBlocking {
        result = userCore.getUser(id)
    }

    @When("the user is created")
    fun theUserIsCreated() = runBlocking {
        coEvery { userRepositoryPort.saveUser(any()) } returns user!!
        try {
            result = userCore.create(user!!)
        } catch (e: Exception) {
            thrownException = e
        }
    }

    @When("the user is updated")
    fun theUserIsUpdated() = runBlocking {
        try {
            result = userCore.update(user!!)
        } catch (e: Exception) {
            thrownException = e
        }
    }

    @Then("the user with id {long} is returned")
    fun theUserWithIdIsReturned(id: Long) {
        result!!.id shouldBe id
    }

    @Then("the user is saved to the repository")
    fun theUserIsSavedToTheRepository() {
        result shouldBe user
        coVerify { userRepositoryPort.saveUser(user!!) }
    }

    @Then("the user is updated in the repository")
    fun theUserIsUpdatedInTheRepository() {
        result shouldBe user
        coVerify { userRepositoryPort.updateUser(user!!) }
    }

    @Then("an exception is thrown")
    fun anExceptionIsThrown() {
        thrownException shouldNotBe null
    }

    @Then("a NotFoundException is thrown")
    fun aNotFoundExceptionIsThrown() {
        thrownException.shouldBeInstanceOf<NotFoundException>()
    }
}
