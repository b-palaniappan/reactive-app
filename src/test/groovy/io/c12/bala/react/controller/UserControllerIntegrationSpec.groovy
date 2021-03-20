package io.c12.bala.react.controller

import io.c12.bala.react.dto.UserDto
import io.c12.bala.react.entity.User
import io.c12.bala.react.repository.UserRepository
import io.c12.bala.react.service.UserServiceImpl
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.blockhound.BlockHound
import reactor.core.publisher.Flux
import spock.lang.Specification
import spock.mock.DetachedMockFactory

/**
 * Example code.
 * https://github.com/spockframework/spock/tree/master/spock-spring/boot2-test/src/test/groovy/org/spockframework/boot2
 *
 * Run WebFlux test with security disabled.
 */
@WebFluxTest(controllers = [UserController], excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration.class])
@Import(UserServiceImpl.class)
class UserControllerIntegrationSpec extends Specification {

    @Autowired
    WebTestClient webTestClient

    @Autowired
    UserRepository userRepository

    @Autowired
    ModelMapper modelMapper

    def setupSpec() {
        BlockHound.install()
    }

    def "get all users from DB"() {
        when: "controller method is called to get all users"
        webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("\$.[0].id").isEqualTo("6czbZTolSKSwf8P0jGXcb")
                .jsonPath("\$.[0].firstName").isEqualTo("John")
                .jsonPath("\$.[0].lastName").isEqualTo("Doe")
                .jsonPath("\$.[0].userId").isEqualTo("john.doe")
                .jsonPath("\$.[0].emailId").isEqualTo("john.doe@c12.io")

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findAll() >> Flux.just(User.builder().id("6czbZTolSKSwf8P0jGXcb").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build())
        1 * modelMapper.map(_ as User, UserDto.class) >> UserDto.builder().id("6czbZTolSKSwf8P0jGXcb").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build()
    }

    def "get all users from DB returns two user"() {
        when:
        webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("\$.[?(@.id=='6czbZTolSKSwf8P0jGXcb')]").exists()
                .jsonPath("\$.[?(@.id=='9YX7n4AxOturpO3KNO3Pm')]").exists()

        then:
        1 * userRepository.findAll() >> Flux.just(User.builder().id("6czbZTolSKSwf8P0jGXcb").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build(),
                User.builder().id("9YX7n4AxOturpO3KNO3Pm").firstName("Jack").lastName("Reacher").userId("jack.reacher").emailId("jack.reacher@c12.io").build())
        2 * modelMapper.map(_ as User, UserDto.class) >> UserDto.builder().id("6czbZTolSKSwf8P0jGXcb").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build() >>
                UserDto.builder().id("9YX7n4AxOturpO3KNO3Pm").firstName("Jack").lastName("Reacher").userId("jack.reacher").emailId("jack.reacher@c12.io").build()
    }

    /**
     * Mock and Inject beans for testing
     */
    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean
        UserRepository userRepository() {
            detachedMockFactory.Mock(UserRepository)
        }

        @Bean
        ModelMapper modelMapper() {
            detachedMockFactory.Mock(ModelMapper)
        }
    }
}
