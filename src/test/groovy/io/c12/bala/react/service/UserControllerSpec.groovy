package io.c12.bala.react.service

import io.c12.bala.react.dto.UserDto
import io.c12.bala.react.repository.UserRepository
import org.modelmapper.ModelMapper
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    ModelMapper modelMapper = Mock()

    UserService userService = new UserServiceImpl(modelMapper, userRepository)

    private UserDto userDto = UserDto.builder().firstName("john").lastName("doe").emailId("john@c12.io").userId("john_doe").build()

    def "Get all users return flux of UserDto"() {
        when:
        StepVerifier.create(userService.getAllUsers())
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete()

        then:
        1 * userRepository.findAll() >> Flux.just(userDto)
    }
}
