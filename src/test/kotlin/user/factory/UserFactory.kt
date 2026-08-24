package user.factory

import features.user.domain.User

class UserFactory {

    companion object {

        fun createUser(
            id: Long = 1,
            firstName: String = "John",
            lastName: String = "Doe",
            zipCode: Int = 86920,
            street: String = "Main Street 1",
            city: String = "Berlin"
        ): User = User(
            id,
            firstName,
            lastName,
            zipCode,
            street,
            city
        )
    }
}
