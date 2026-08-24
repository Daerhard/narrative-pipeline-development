package features.user.persistence

import exceptions.NotFoundException
import pipelineExtensions.orElseThrow
import org.springframework.stereotype.Component
import pipelineExtensions.logInfo
import features.user.domain.User
import features.user.domain.ports.UserRepositoryPort

@Component
class UserRepositoryAdapter(
    private val userRepository: UserRepository
): UserRepositoryPort {

    override suspend fun getUser(id: Long): User =
        id.logInfo { "Get user by id $id" }
            .run { userRepository.findById(this) }
            .orElseThrow { NotFoundException("User not found") }
            .toDomainModel()

    override suspend fun saveUser(user: User): User =
        user.logInfo { "Save user with id ${user.id}" }
            .toEntity()
            .let { entity -> userRepository.save(entity) }
            .toDomainModel()

    override suspend fun updateUser(user: User): User =
        user.logInfo { "Update user with id ${user.id}" }
            .toEntity()
            .let { entity -> userRepository.save(entity) }
            .orElseThrow { NotFoundException("User not found") }
            .toDomainModel()

    override suspend fun existsById(user: User): Boolean = userRepository.existsById(user.id)
}