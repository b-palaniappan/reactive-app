package io.c12.bala.react.config;

import io.c12.bala.react.service.AuthUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity()           // Enables security at controller method level
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/v1/users", "/api/v1/users/**").hasRole(ReactiveConstant.SECURITY_ROLE_ADMIN)     // Only admin can do POST
            .pathMatchers(HttpMethod.GET, "/api/v1/users", "/api/v1/users/**").hasAnyRole(ReactiveConstant.SECURITY_ROLE_USER, ReactiveConstant.SECURITY_ROLE_ADMIN)       // user can only do GET
            .anyExchange().authenticated()
            .and().formLogin()
            .and().httpBasic()
            .and().formLogin().disable()
            .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(AuthUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }
}
