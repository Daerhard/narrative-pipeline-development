package user.persistence

import exceptions.NotFoundException
import features.user.persistence.User
import features.user.persistence.UserRepository
import features.user.persistence.UserRepositoryAdapter
import features.user.persistence.toEntity
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import features.user.domain.User as DomainUser
import user.factory.UserFactory

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("user/persistence")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "user.persistence,cucumber")
class UserRepositoryAdapterFeature

class UserRepositoryAdapterTest {

    private val userRepository: UserRepository = mockk()
    private val adapter = UserRepositoryAdapter(userRepository)

    private var domainUser: DomainUser? = null
    private var persistenceUser: User? = null
    private var result: DomainUser? = null
    private var existsResult: Boolean? = null
    private var thrownException: Exception? = null

    @Before
    fun reset() {
        domainUser = null
        persistenceUser = null
        result = null
        existsResult = null
        thrownException = null
    }

    @Given("the database contains a user with id {long}")
    fun theDatabaseContainsAUserWithId(id: Long) {
        domainUser = UserFactory.createUser(id = id)
        persistenceUser = domainUser!!.toEntity()
        coEvery { userRepository.findById(id) } returns persistenceUser
        coEvery { userRepository.existsById(id) } returns true
    }

    @Given("the database does not contain a user with id {long}")
    fun theDatabaseDoesNotContainAUserWithId(id: Long) {
        domainUser = UserFactory.createUser(id = id)
        coEvery { userRepository.findById(id) } returns null
        coEvery { userRepository.existsById(id) } returns false
    }

    @Given("a domain user to persist")
    fun aDomainUserToPersist() {
        domainUser = UserFactory.createUser()
        persistenceUser = domainUser!!.toEntity()
        coEvery { userRepository.save(any()) } returns persistenceUser!!
    }

    @When("I fetch the user with id {long}")
    fun iFetchTheUserWithId(id: Long) = runBlocking {
        try {
            result = adapter.getUser(id)
        } catch (e: Exception) {
            thrownException = e
        }
    }

    @When("the user is saved")
    fun theUserIsSaved() = runBlocking {
        result = adapter.saveUser(domainUser!!)
    }

    @When("the adapter updates the user")
    fun theAdapterUpdatesTheUser() = runBlocking {
        result = adapter.updateUser(domainUser!!)
    }

    @When("I check if the user with id {long} exists")
    fun iCheckIfTheUserWithIdExists(id: Long) = runBlocking {
        existsResult = adapter.existsById(domainUser!!)
    }

    @Then("the domain user with id {long} is returned")
    fun theDomainUserWithIdIsReturned(id: Long) {
        result!!.id shouldBe id
    }

    @Then("the repository throws a NotFoundException")
    fun theRepositoryThrowsANotFoundException() {
        thrownException.shouldBeInstanceOf<NotFoundException>()
    }

    @Then("the repository saved the entity and the domain user is returned")
    fun theRepositorySavedTheEntityAndDomainUserIsReturned() {
        result shouldBe domainUser
        coVerify { userRepository.save(any()) }
    }

    @Then("the existence check returns true")
    fun theExistenceCheckReturnsTrue() {
        existsResult shouldBe true
    }

    @Then("the existence check returns false")
    fun theExistenceCheckReturnsFalse() {
        existsResult shouldBe false
    }
}
