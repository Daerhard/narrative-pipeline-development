package user.domain

import features.user.domain.UserService
import features.user.domain.ZIP_LIMIT
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import user.factory.UserFactory

class UserServiceTest {

    private val userService = UserService()

    @Test
    fun `validateUser returns true when zip code is below limit`() {
        val user = UserFactory.createUser(zipCode = ZIP_LIMIT - 1)

        userService.validateUser(user) shouldBe true
    }

    @Test
    fun `validateUser returns false when zip code equals limit`() {
        val user = UserFactory.createUser(zipCode = ZIP_LIMIT)

        userService.validateUser(user) shouldBe false
    }

    @Test
    fun `validateUser returns false when zip code exceeds limit`() {
        val user = UserFactory.createUser(zipCode = ZIP_LIMIT + 1)

        userService.validateUser(user) shouldBe false
    }
}
