package io.c12.bala.react.repository;

import io.c12.bala.react.entity.UserAuth;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserAuthRepository extends ReactiveMongoRepository<UserAuth, String> {

    Mono<UserAuth> findByUsername(String username);
}
