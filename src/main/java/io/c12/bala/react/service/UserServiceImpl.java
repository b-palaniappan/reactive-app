package io.c12.bala.react.service;

import io.c12.bala.react.dto.UserDto;
import io.c12.bala.react.entity.User;
import io.c12.bala.react.exception.UserNotFoundException;
import io.c12.bala.react.repository.UserRepository;
import io.c12.bala.react.utils.NanoIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public Mono<UserDto> addUser(User user) {
        log.info("Adding a user to db - {}", user);
        user.setId(NanoIdUtils.randomNanoId());
        Mono<User> savedUser = userRepository.save(user);
        return savedUser.map(u -> modelMapper.map(u, UserDto.class));
    }

    @Override
    public Mono<UserDto> updateUser(String id, User user) {
        log.info("Update user for id {}", id);
        return userRepository.existsById(id).flatMap(x -> {
            log.info("User Id {} exists - {}", id, x);
            if (x) {
                user.setId(id);
                return userRepository.save(user);
            } else {
                return Mono.error((Throwable) new UserNotFoundException());
            }
        }).map(u -> modelMapper.map(u, UserDto.class));
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return userRepository.existsById(id).flatMap(x -> {
            log.info("User Id {} exists - {}", id, x);
            if (x) {
                return userRepository.deleteById(id);
            } else {
                return Mono.error((Throwable) new UserNotFoundException());
            }
        });
    }

    @Override
    public Mono<UserDto> getUserById(String id) {
        return userRepository.findById(id).map(u -> modelMapper.map(u, UserDto.class));
    }

    @Override
    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll().map(u -> modelMapper.map(u, UserDto.class));
    }

}
