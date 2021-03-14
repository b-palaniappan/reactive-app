package io.c12.bala.react.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity()           // Enables security at controller method level
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/api/v1/users/**").hasRole(ReactiveConstant.SECURITY_ROLE_ADMIN)     // Only admin can do POST
                .pathMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole(ReactiveConstant.SECURITY_ROLE_USER, ReactiveConstant.SECURITY_ROLE_ADMIN)       // user can only do GET
                .anyExchange().authenticated()
                .and().formLogin()
                .and().httpBasic()
                .and().build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("Password@123"))
                .roles(ReactiveConstant.SECURITY_ROLE_USER)
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("Password@123"))
                .roles(ReactiveConstant.SECURITY_ROLE_ADMIN)
                .build();

        return new MapReactiveUserDetailsService(user, admin);
    }
}
