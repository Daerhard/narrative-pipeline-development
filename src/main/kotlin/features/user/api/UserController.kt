package features.user.api

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import features.user.domain.ports.UserControllerPort

@RestController
@RequestMapping("/features/user")
class UserController(
    private val userControllerPort: UserControllerPort
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun create(userId: Long): User =
        userControllerPort.getUser(userId).toApiModel()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@Valid @RequestBody user: User): User =
        user.toDomainModel()
        .let { user -> userControllerPort.create(user) }
        .toApiModel()

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun update(@Valid @RequestBody trade: User): User =
        trade.toDomainModel()
            .let { trade -> userControllerPort.update(trade) }
            .toApiModel()
}