package io.c12.bala.react.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.blockhound.BlockHound
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserControllerIntegrationSpec extends Specification {

    @Autowired
    private WebTestClient client

    def setupSpec() {
        BlockHound.install()
    }

    def "get all user and when user is not authorized"() {
        expect:
        client
                .get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isUnauthorized()
    }

}
