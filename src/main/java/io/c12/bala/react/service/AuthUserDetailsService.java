package io.c12.bala.react.service;

import io.c12.bala.react.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements ReactiveUserDetailsService {

    private final UserAuthRepository userAuthRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userAuthRepository.findByUsername(username)
                .cast(UserDetails.class);
    }
}
