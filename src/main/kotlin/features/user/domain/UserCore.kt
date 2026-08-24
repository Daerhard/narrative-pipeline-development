package features.user.domain

import exceptions.NotFoundException
import pipelineExtensions.orElseThrow
import pipelineExtensions.suspendProceedIf
import org.springframework.stereotype.Service
import features.user.domain.ports.UserControllerPort
import features.user.domain.ports.UserRepositoryPort

@Service
class UserCore(
    private val userRepositoryPort: UserRepositoryPort,
    private val userService: UserService
): UserControllerPort {

    override suspend fun getUser(id: Long): User =
        userRepositoryPort.getUser(id)

    override suspend fun create(user: User): User =
        with(userService) {
            user.suspendProceedIf { user -> validateUser(user) }
                .orElseThrow { Exception("User with id ${user.id} is not valid") }
                .let { user -> userRepositoryPort.saveUser(user) }
        }

    override suspend fun update(user: User): User =
        with(userService) {
            user.suspendProceedIf { user -> validateUser(user) }
                .orElseThrow { Exception("User with id ${user.id} is not valid") }
                .suspendProceedIf { user -> userRepositoryPort.existsById(user) }
                .orElseThrow{ NotFoundException("User does not exist") }
                .let { user -> userRepositoryPort.updateUser(user) }
        }
}
