package io.c12.bala.react.service;

import io.c12.bala.react.dto.UserDto;
import io.c12.bala.react.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(User user);

    Mono<UserDto> updateUser(String id, User user);

    Mono<Void> deleteUser(String id);

    Mono<UserDto> getUserById(String id);

    Flux<UserDto> getAllUsers();
}
