package features.user.domain.ports

import features.user.domain.User

interface UserRepositoryPort {
    suspend fun getUser(id: Long): User
    suspend fun saveUser(user: User): User
    suspend fun updateUser(user: User): User
    suspend fun existsById(user: User): Boolean
}