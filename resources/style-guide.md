# Narrative Pipeline Development

A development style for Kotlin that makes business behavior immediately readable in production code — and directly traceable to BDD specifications.

## The Problem

Business behavior is often buried inside control flow:

```kotlin
if (userRepository.existsById(user.id)) {
    if (validateUser(user)) {
        userRepository.update(user)
    } else {
        throw NotValidException()
    }
} else {
    throw NotFoundException()
}
```

The behavior is there, but the reader has to reconstruct it.

## The NPD Approach

```kotlin
override suspend fun update(user: User): User =
    with(userService) {
        user
            .suspendProceedIf { user -> validateUser(user) }
            .orElseThrow { Exception("User is not valid") }
            .suspendProceedIf { user -> userRepositoryPort.existsById(user) }
            .orElseThrow { NotFoundException("User does not exist") }
            .let { user -> userRepositoryPort.updateUser(user) }
    }
```

The flow is the behavior. No reconstruction required:

```
Validate user
    ↓ invalid → Exception
Check user exists
    ↓ missing → NotFoundException
Update user
```

## Pipeline Extensions

NPD is supported by a small vocabulary of extension functions in `pipelineExtensions`:

**`ChainExtensions.kt`** — control flow:

```kotlin
fun <T> T.proceedIf(check: (T) -> Boolean): T? =
    if (check(this)) this else null

suspend fun <T> T.suspendProceedIf(check: suspend (T) -> Boolean): T? =
    if (check(this)) this else null

fun <T> T?.orElseThrow(exception: () -> Exception): T =
    this ?: throw exception()
```

`proceedIf` is used in blocking (non-reactive) flows. `suspendProceedIf` is used in coroutine-based (reactive) flows.

**`LogExtensions.kt`** — observable behavior, without breaking the chain:

```kotlin
fun <T> T.logInfo(message: (T) -> String): T = also { logger.info(message(it)) }
fun <T> T.logWarning(message: (T) -> String): T = also { logger.warn(message(it)) }
fun <T> T.logError(message: (T) -> String): T = also { logger.error(message(it)) }
```

| Operation | Behavioral role | Type |
|---|---|---|
| `proceedIf { }` | Guard: let the value continue only if the condition holds | `T → T?` |
| `orElseThrow { }` | Resolve absence with a domain exception | `T? → T` |
| `logInfo/Warning/Error { }` | Observe without affecting the value | `T → T` |
| `let` | Transform to the next value | `T → R` |
| `also` | Observe without affecting the value (standard Kotlin) | `T → T` |

`proceedIf` / `suspendProceedIf` is a condition gate, not a predicate filter. The condition does not have to be about the value itself — it can be any external check:

```kotlin
user.suspendProceedIf { user -> userRepositoryPort.existsById(user) }
user.suspendProceedIf { user -> permissionCache.hasPermission(user.id) }
user.suspendProceedIf { featureFlags.isEnabled("new-user-flow") }
```

## Behavioral Traceability

The production flow and the test descriptions share a vocabulary. The path from behavior to test is short.

**Production (`UserCore.kt`):**
```kotlin
user
    .suspendProceedIf { user -> validateUser(user) }
    .orElseThrow { Exception("User is not valid") }
    .suspendProceedIf { user -> userRepositoryPort.existsById(user) }
    .orElseThrow { NotFoundException("User does not exist") }
    .let { user -> userRepositoryPort.updateUser(user) }
```

**Feature file (`UserCore.feature`):**
```gherkin
Scenario: Update a valid existing user
  Given a core user with zip code 86920
  And the user exists in the repository
  When the user is updated
  Then the user is updated in the repository

Scenario: Reject updating a user that does not exist
  Given a core user with zip code 86920
  And the user does not exist in the repository
  When the user is updated
  Then a NotFoundException is thrown

Scenario: Reject updating a user with an invalid zip code
  Given a core user with zip code 100001
  When the user is updated
  Then an exception is thrown
```

**Step definitions (`UserCoreTest.kt`):**
```kotlin
@And("the user exists in the repository")
fun theUserExistsInRepository() {
    coEvery { userRepositoryPort.existsById(user!!) } returns true
    coEvery { userRepositoryPort.updateUser(user!!) } returns user!!
}

@When("the user is updated")
fun theUserIsUpdated() = runBlocking {
    try {
        result = userCore.update(user!!)
    } catch (e: Exception) {
        thrownException = e
    }
}

@Then("a NotFoundException is thrown")
fun aNotFoundExceptionIsThrown() {
    thrownException.shouldBeInstanceOf<NotFoundException>()
}
```

Each branch in the production pipeline corresponds directly to a Gherkin scenario. A developer can move from code to specification without reconstructing what the code does.

## Test Structure

Tests are organized by architectural layer, each with its own Cucumber feature suite:

| Layer | Feature file | What is mocked |
| --- | --- | --- |
| Domain rules | `UserService.feature` | nothing — pure logic |
| Use case orchestration | `UserCore.feature` | `UserRepositoryPort` |
| Persistence adapter | `UserRepositoryAdapter.feature` | `UserRepository` |
| HTTP controller | `UserControllerTest.kt` | `UserControllerPort` |

Each layer is tested in isolation. A shared `UserFactory` in `user/factory/` provides default test objects across all suites.

## Meaningful Functions

Functions should express what the system is doing, not how it is technically implemented.

Prefer:
```kotlin
validateUser(user)
existsById(user)
updateUser(user)
```

Over spreading implementation details into the behavioral flow:
```kotlin
repository.findById(user.id).map { mapper.map(it) }
```

A meaningful function answers: **what is the system doing?**

## Explicit Lambda Parameter Names

Name lambda parameters explicitly whenever the parameter is used inside the lambda. Do not rely on `it` or on outer-scope variables of the same name.

**Prefer:**
```kotlin
user.suspendProceedIf { user -> validateUser(user) }
    .let { user -> userRepositoryPort.updateUser(user) }
```

**Avoid:**
```kotlin
user.suspendProceedIf { validateUser(it) }        // unclear what flows into validateUser
user.suspendProceedIf { validateUser(user) }      // shadows outer variable silently
    .let { userRepositoryPort.updateUser(it) }    // it has no name — what is it?
```

The goal is that a reader can follow *what is flowing* through each step without looking back at earlier lines. When the parameter is genuinely unused, omit it entirely (no `_` needed for simple lambdas):

```kotlin
user.suspendProceedIf { featureFlags.isEnabled("new-flow") }
```

## What NPD Is Not

NPD is not about putting everything into a chain. If a chain makes behavior *harder* to read, use `if`/`when` instead. Behavioral clarity over pipeline syntax.

NPD is not an architecture. It works alongside Hexagonal Architecture, Clean Architecture, or any other style. Architecture answers *where* responsibility belongs. NPD answers *how* to express it so the behavior is immediately understandable.
