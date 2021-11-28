package io.c12.bala.react.service;

import io.c12.bala.react.dto.UserDto;
import io.c12.bala.react.entity.User;
import io.c12.bala.react.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public Mono<UserDto> addUser(UserDto userDto) {
        log.info("Adding a user to db - {}", userDto);
        User user = modelMapper.map(userDto, User.class);       // convert DTO to entity before we store teh DB.
        user.setId(UUID.randomUUID().toString());
        return userRepository.save(user).map(u -> modelMapper.map(u, UserDto.class));       // save and convert to UserDto class
    }

    @Override
    public Mono<UserDto> updateUser(UserDto userDto) {
        log.info("Update user. new user info {}", userDto);
        User user = modelMapper.map(userDto, User.class);       // convert DTO to entity before we update the DB.
        return findUserById(user.getId())
            .flatMap(u -> userRepository.save(user))
            .map(u -> modelMapper.map(u, UserDto.class));    // convert back to dto
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return findUserById(id)
            .flatMap(userRepository::delete);
    }

    @Override
    public Mono<UserDto> getUserById(String id) {
        return findUserById(id).map(u -> modelMapper.map(u, UserDto.class));
    }

    @Override
    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll().map(u -> modelMapper.map(u, UserDto.class));
    }

    // General exception handling for User not found
    private <T> Mono<T> monoResponseStatusNotFoundException() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Mono<User> findUserById(String id) {
        return userRepository.findById(id)
            .switchIfEmpty(monoResponseStatusNotFoundException());      // Throws 404 exception if not found for id.
    }

}
