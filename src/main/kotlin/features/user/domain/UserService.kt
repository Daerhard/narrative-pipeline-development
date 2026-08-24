package features.user.domain

const val ZIP_LIMIT = 100000

class UserService {
    fun validateUser(user: User): Boolean = user.zipCode < ZIP_LIMIT
}