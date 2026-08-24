package features.customer.api

import features.customer.domain.Customer
import features.customer.domain.ports.CustomerControllerPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/features/customer")
class CustomerController(
    private val customerControllerPort: CustomerControllerPort,
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getCustomer(customerId: Long): Customer =
        customerControllerPort.getCustomer(customerId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody customer: Customer): Customer =
        customerControllerPort.create(customer)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun update(@Valid @RequestBody customer: Customer): Customer =
        customerControllerPort.update(customer)
}
