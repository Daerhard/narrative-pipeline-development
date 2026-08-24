package features.user.domain.ports

import features.user.domain.User

interface UserControllerPort {
    suspend fun getUser(id: Long): User
    suspend fun create(user: User): User
    suspend fun update(user: User): User
}