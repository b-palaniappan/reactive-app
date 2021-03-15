package io.c12.bala.react.controller

import io.c12.bala.react.dto.UserDto
import io.c12.bala.react.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import reactor.blockhound.BlockHound
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class UserControllerSpec extends Specification {

    UserService userService = Mock()

    UserController userController = new UserController(userService)

    UserDto userDto = UserDto.builder().id("b6EOm8CbKt_meGjNz1tm9").firstName("John").lastName("Doe").emailId("john@c12.io").userId("john_doe").build()

    def setupSpec() {
        BlockHound.install()
    }

    def "add user return mono of user"() {
        when:
        StepVerifier.create(userController.addUser(userDto))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * userService.addUser(userDto) >> Mono.just(userDto)
    }

    def "add user failed. return Mono of empty"() {
        when:
        StepVerifier.create(userController.addUser(userDto))
                .expectSubscription()
                .verifyComplete()

        then:
        1 * userService.addUser(userDto) >> Mono.empty()
    }

    def "update user return mono of user"() {
        when:
        StepVerifier.create(userController.updateUser("b6EOm8CbKt_meGjNz1tm9", userDto))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * userService.updateUser(userDto) >> Mono.just(userDto)
    }

    def "update user user not found for the id throws error"() {
        when:
        StepVerifier.create(userController.updateUser("nQ2074Yav7724hRCxfRfT", userDto))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify()

        then:
        1 * userService.updateUser(userDto) >> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
    }

    def "get all users return flux of user"() {
        when:
        StepVerifier.create(userController.getAllUsers())
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * userService.getAllUsers() >> Flux.just(userDto)
    }

    def "get all users return flux of empty"() {
        when:
        StepVerifier.create(userController.getAllUsers())
                .expectSubscription()
                .verifyComplete()

        then:
        1 * userService.getAllUsers() >> Flux.empty()
    }

    def "get user by id return mono of user"() {
        when:
        StepVerifier.create(userController.getUser("b6EOm8CbKt_meGjNz1tm9"))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * userService.getUserById("b6EOm8CbKt_meGjNz1tm9") >> Mono.just(userDto)
    }

    def "get user by id when user not found for the id"() {
        when:
        StepVerifier.create(userController.getUser("nQ2074Yav7724hRCxfRfT"))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify()

        then: "getUserById throws an error"
        1 * userService.getUserById("nQ2074Yav7724hRCxfRfT") >> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
    }

    def "delete user by user id return empty mono"() {
        when:
        StepVerifier.create(userController.deleteUser("b6EOm8CbKt_meGjNz1tm9"))
                .expectSubscription()
                .verifyComplete()

        then:
        1 * userService.deleteUser("b6EOm8CbKt_meGjNz1tm9") >> Mono.empty()
    }

    def "delete user by user id throws error when user not found for id"() {
        when:
        StepVerifier.create(userController.deleteUser("nQ2074Yav7724hRCxfRfT"))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify()

        then:
        1 * userService.deleteUser("nQ2074Yav7724hRCxfRfT") >> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
    }

}
