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
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.blockhound.BlockHound
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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

    def "add a new user returns Mono of userDto"() {
        given: "create mock user to be saved"
        UserDto userDto = UserDto.builder().firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build()

        when: "controller method is called to add a new user"
        webTestClient
                .post().uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("\$.id").isEqualTo("NcyBK0F0Tbm38g8keKH30")

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.save(_ as User) >> Mono.just(User.builder().id("NcyBK0F0Tbm38g8keKH30").firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build())
    }

    def "add a new user with missing required values"() {
        given: "create mock user to be saved"
        UserDto userDto = UserDto.builder().firstName("John").lastName("Doe").emailId("john@cloud12.io").build()

        when: "controller method is called to add a new user"
        webTestClient
                .post().uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("\$.error").isNotEmpty()
                .jsonPath("\$.error").isEqualTo("Bad Request")
                .jsonPath("\$.status").isEqualTo(400)

        then: "verify if the repository and modelMapper are NOT called"
        0 * userRepository.save(_ as User)
    }

    def "update a user return a Mono of UserDto"() {
        given: "create mock user to be updated"
        UserDto userDto = UserDto.builder().firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build()

        when: "controller method is called to update a user"
        webTestClient
                .put().uri("/api/v1/users/{id}", "fzFIu8jUe09d2lXqJf9x5")
                .body(BodyInserters.fromValue(userDto))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("\$.id").isEqualTo("fzFIu8jUe09d2lXqJf9x5")

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("fzFIu8jUe09d2lXqJf9x5") >> Mono.just(User.builder().id("fzFIu8jUe09d2lXqJf9x5").firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build())
        1 * userRepository.save(_ as User) >> Mono.just(User.builder().id("fzFIu8jUe09d2lXqJf9x5").firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build())
    }

    def "update a user add interests return a Mono of UserDto"() {
        given: "create mock user to be updated"
        UserDto userDto = UserDto.builder().firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").interests(["iot", "home automation"]).build()

        when: "controller method is called to update a user"
        webTestClient
                .put().uri("/api/v1/users/{id}", "fzFIu8jUe09d2lXqJf9x5")
                .body(BodyInserters.fromValue(userDto))
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("\$.id").isEqualTo("fzFIu8jUe09d2lXqJf9x5")
                .jsonPath("\$.interests").exists()
                .jsonPath("\$.interests").isArray()

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("fzFIu8jUe09d2lXqJf9x5") >> Mono.just(User.builder().id("fzFIu8jUe09d2lXqJf9x5").firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build())
        1 * userRepository.save(_ as User) >> Mono.just(User.builder().id("fzFIu8jUe09d2lXqJf9x5").firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").interests(["iot", "home automation"]).build())
    }

    def "update a user but user not found for the ID"() {
        given: "create mock user to be updated"
        UserDto userDto = UserDto.builder().firstName("John").lastName("Doe").userId("john.doe").emailId("john@cloud12.io").build()

        when: "controller method is called to update a user"
        webTestClient
                .put().uri("/api/v1/users/{id}", "PsfcHujR0goR3KFHqIBIj")
                .body(BodyInserters.fromValue(userDto))
                .exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("\$.error").isNotEmpty()
                .jsonPath("\$.error").isEqualTo("Not Found")
                .jsonPath("\$.status").isEqualTo(404)

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("PsfcHujR0goR3KFHqIBIj") >> Mono.empty()
        0 * userRepository.save(_ as User)
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
    }

    def "get all users from DB returns two user"() {
        when: "controller method is called to get all users"
        webTestClient
                .get().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("\$.[?(@.id=='6czbZTolSKSwf8P0jGXcb')]").exists()
                .jsonPath("\$.[?(@.id=='9YX7n4AxOturpO3KNO3Pm')]").exists()

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findAll() >> Flux.just(User.builder().id("6czbZTolSKSwf8P0jGXcb").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build(),
                User.builder().id("9YX7n4AxOturpO3KNO3Pm").firstName("Jack").lastName("Reacher").userId("jack.reacher").emailId("jack.reacher@c12.io").build())
    }

    def "find user by id return Mono of UserDto"() {
        when: "controller method is called to find a user by id"
        webTestClient
                .get().uri("/api/v1/users/{id}", "oApNCL9e3nfA7S6bQsz3m")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("\$.id").isEqualTo("oApNCL9e3nfA7S6bQsz3m")

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("oApNCL9e3nfA7S6bQsz3m") >> Mono.just(User.builder().id("oApNCL9e3nfA7S6bQsz3m").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build())
    }

    def "find user by id when user by id not found"() {
        when: "controller method is called to find a user by id"
        webTestClient
                .get().uri("/api/v1/users/{id}", "oApNCL9e3nfA7S6bQsz3m")
                .exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("\$.error").isNotEmpty()
                .jsonPath("\$.error").isEqualTo("Not Found")
                .jsonPath("\$.status").isEqualTo(404)

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("oApNCL9e3nfA7S6bQsz3m") >> Mono.empty()
    }

    def "delete user by id return No Content"() {
        when: "controller method is called to delete a user by id"
        webTestClient
                .delete().uri("/api/v1/users/{id}", "2RtzDAA01725s_zGgcjlQ")
                .exchange().expectStatus().isNoContent()

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("2RtzDAA01725s_zGgcjlQ") >> Mono.just(User.builder().id("2RtzDAA01725s_zGgcjlQ").firstName("John").lastName("Doe").userId("john.doe").emailId("john.doe@c12.io").build())
        1 * userRepository.delete(_ as User) >> Mono.empty()
    }

    def "delete user by id when user by id not found"() {
        when: "controller method is called to delete a user by id"
        webTestClient
                .delete().uri("/api/v1/users/{id}", "P3EvOAs9kM3BQ8H43kXkN")
                .exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("\$.error").isNotEmpty()
                .jsonPath("\$.error").isEqualTo("Not Found")
                .jsonPath("\$.status").isEqualTo(404)

        then: "verify if the repository and modelMapper are called"
        1 * userRepository.findById("P3EvOAs9kM3BQ8H43kXkN") >> Mono.empty()
        0 * userRepository.delete(_ as User)
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
            // Using real model mapper instead of mocking it up.
            return new ModelMapper()
        }
    }
}
