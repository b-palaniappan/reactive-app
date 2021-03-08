package io.c12.bala.react.service;

import io.c12.bala.react.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> addUser(UserDto userDto);

    Mono<UserDto> updateUser(String id, UserDto userDto);

    Mono<Void> deleteUser(String id);

    Mono<UserDto> getUserById(String id);

    Flux<UserDto> getAllUsers();
}
