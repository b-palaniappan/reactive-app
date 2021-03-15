package io.c12.bala.react.service

import io.c12.bala.react.dto.UserDto
import io.c12.bala.react.entity.User
import io.c12.bala.react.repository.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.web.server.ResponseStatusException
import reactor.blockhound.BlockHound
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    ModelMapper modelMapper = Mock()

    UserService userService = new UserServiceImpl(modelMapper, userRepository)

    // Test data
    UserDto userDto = UserDto.builder().id("b6EOm8CbKt_meGjNz1tm9").firstName("John").lastName("Doe").emailId("john@c12.io").userId("john_doe").build()
    User user = User.builder().id("b6EOm8CbKt_meGjNz1tm9").firstName("John").lastName("Doe").emailId("john@c12.io").userId("john_doe").build()

    def setupSpec() {
        BlockHound.install()
    }

    def "Add user successfully"() {
        when:
        StepVerifier.create(userService.addUser(userDto))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * modelMapper.map(userDto, User.class) >> user
        1 * userRepository.save(user) >> Mono.just(user)
        1 * modelMapper.map(user, UserDto.class) >> userDto
    }

    def "Add user but an error is thrown"() {
        when:
        StepVerifier.create(userService.addUser(userDto))
                .expectSubscription()
                .verifyComplete()

        then:
        1 * modelMapper.map(userDto, User.class) >> user
        1 * userRepository.save(user) >> Mono.empty()
    }

    def "Update user by id and updated user info"() {
        when:
        StepVerifier.create(userService.updateUser(userDto))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * modelMapper.map(userDto, User.class) >> user
        1 * userRepository.findById("b6EOm8CbKt_meGjNz1tm9") >> Mono.just(user)
        1 * userRepository.save(user) >> Mono.just(user)
        1 * modelMapper.map(user, UserDto) >> userDto
    }

    def "Update user by id and user not found in"() {
        when:
        StepVerifier.create(userService.updateUser(userDto))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify()

        then:
        1 * modelMapper.map(userDto, User.class) >> user
        1 * userRepository.findById("b6EOm8CbKt_meGjNz1tm9") >> Mono.empty()
        0 * userRepository.save(_)
        0 *  modelMapper.map(_, _)
    }

    def "Delete user by id"() {
        when:
        StepVerifier.create(userService.deleteUser("b6EOm8CbKt_meGjNz1tm9"))
                .expectSubscription()
                .verifyComplete()

        then:
        1 * userRepository.findById("b6EOm8CbKt_meGjNz1tm9") >> Mono.just(user)
        1 * userRepository.delete(user) >> Mono.empty()
    }

    def "Delete user by id when user is not found"() {
        when:
        StepVerifier.create(userService.deleteUser("b6EOm8CbKt_meGjNz1tm9"))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify()

        then:
        1 * userRepository.findById("b6EOm8CbKt_meGjNz1tm9") >> Mono.empty()
        0 * userRepository.delete(_)
    }

    def "Get user by id return mono of UserDto"() {
        when:
        StepVerifier.create(userService.getUserById("b6EOm8CbKt_meGjNz1tm9"))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * userRepository.findById("b6EOm8CbKt_meGjNz1tm9") >> Mono.just(user)
        1 * modelMapper.map(user, UserDto.class) >> userDto
    }

    def "Get user by id for id not found error is thrown"() {
        when:
        StepVerifier.create(userService.getUserById("b6EOm8CbKt_meGjNz1tm9"))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify()

        then:
        1 * userRepository.findById("b6EOm8CbKt_meGjNz1tm9") >> Mono.empty()
        0 * modelMapper.map(_, _)
    }

    def "Get all users return flux of UserDto"() {
        when:
        StepVerifier.create(userService.getAllUsers())
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then: "if repository and model mapper called"
        1 * userRepository.findAll() >> Flux.just(user)
        1 * modelMapper.map(user, userDto.class) >> userDto
    }

    def "Get all users return flux of multiple UserDto"() {
        given: "create mock UserDto and User"
        UserDto userDto1 = UserDto.builder().firstName("John").lastName("Doe").emailId("john@c12.io").userId("john_doe").build()
        UserDto userDto2 = UserDto.builder().firstName("Jane").lastName("Walker").emailId("jane@c12.io").userId("jane.w").build()
        User user1 = User.builder().id("b6EOm8CbKt_meGjNz1tm9").firstName("John").lastName("Doe").emailId("john@c12.io").userId("john_doe").build()
        User user2 = User.builder().id("VVDDQy0SGSlnONwYYUd9k").firstName("Jane").lastName("Walker").emailId("jane@c12.io").userId("jane.w").build()

        when:
        StepVerifier.create(userService.getAllUsers())
                .expectSubscription()
                .expectNext(userDto1)
                .expectNext(userDto2)
                .verifyComplete()

        then:
        1 * userRepository.findAll() >> Flux.just(user1, user2)
        1 * modelMapper.map(user1, UserDto.class) >> userDto1
        1 * modelMapper.map(user2, UserDto.class) >> userDto2
    }
}
