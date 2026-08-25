package user.core

import exceptions.NotFoundException
import features.user.domain.UserCore
import features.user.domain.UserService
import features.user.domain.ZIP_LIMIT
import features.user.domain.ports.UserRepositoryPort
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import user.factory.UserFactory

class UserCoreUnitTest {

    private val userRepositoryPort: UserRepositoryPort = mockk()
    private val userService = UserService()
    private val userCore = UserCore(userRepositoryPort, userService)

    @Test
    fun `getUser returns user from repository`() = runTest {
        val user = UserFactory.createUser(id = 1)
        coEvery { userRepositoryPort.getUser(1L) } returns user

        val result = userCore.getUser(1L)

        result shouldBe user
    }

    @Test
    fun `create saves and returns valid user`() = runTest {
        val user = UserFactory.createUser()
        coEvery { userRepositoryPort.saveUser(user) } returns user

        val result = userCore.create(user)

        result shouldBe user
        coVerify { userRepositoryPort.saveUser(user) }
    }

    @Test
    fun `create throws exception when zip code is at limit`() = runTest {
        val user = UserFactory.createUser(zipCode = ZIP_LIMIT)

        val exception = runCatching { userCore.create(user) }.exceptionOrNull()

        exception.shouldBeInstanceOf<Exception>()
    }

    @Test
    fun `update updates and returns user when valid and exists`() = runTest {
        val user = UserFactory.createUser()
        coEvery { userRepositoryPort.existsById(user) } returns true
        coEvery { userRepositoryPort.updateUser(user) } returns user

        val result = userCore.update(user)

        result shouldBe user
        coVerify { userRepositoryPort.updateUser(user) }
    }

    @Test
    fun `update throws exception when zip code is at limit`() = runTest {
        val user = UserFactory.createUser(zipCode = ZIP_LIMIT)

        val exception = runCatching { userCore.update(user) }.exceptionOrNull()

        exception.shouldBeInstanceOf<Exception>()
    }

    @Test
    fun `update throws NotFoundException when user does not exist`() = runTest {
        val user = UserFactory.createUser()
        coEvery { userRepositoryPort.existsById(user) } returns false

        val exception = runCatching { userCore.update(user) }.exceptionOrNull()

        exception.shouldBeInstanceOf<NotFoundException>()
    }
}
