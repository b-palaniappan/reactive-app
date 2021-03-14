package io.c12.bala.react.exception;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Global error handler.
 * Todo: figure out a good way to handle global exception handling for Webflux
 */
@RequiredArgsConstructor
//@Configuration
//@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();
        if (throwable instanceof UserNotFoundException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            DataBuffer dataBuffer;
            try {
                dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(new ApiError(HttpStatus.BAD_REQUEST, Calendar.getInstance(), throwable.getMessage(), "User not found for id", new ArrayList<>())));
            } catch (JsonProcessingException e) {
                dataBuffer = bufferFactory.wrap("".getBytes());
            }
            serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
        }

        serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        serverWebExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        DataBuffer dataBuffer = bufferFactory.wrap("Unknown error".getBytes());
        return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    interface ApiSubError {

    }

    @Data
    @AllArgsConstructor
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
    @JsonTypeIdResolver(LowerCaseClassNameResolver.class)
    public static class ApiError {

        private HttpStatus status;
        private Calendar timestamp;
        private String message;
        private String debugMessage;
        private List<ApiSubError> subErrors;

        private ApiError() {
            timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }

        ApiError(HttpStatus status) {
            this();
            this.status = status;
        }

        ApiError(HttpStatus status, Throwable ex) {
            this();
            this.status = status;
            this.message = "Unexpected error";
            this.debugMessage = ex.getLocalizedMessage();
        }

        ApiError(HttpStatus status, String message, Throwable ex) {
            this();
            this.status = status;
            this.message = message;
            this.debugMessage = ex.getLocalizedMessage();
        }

        private void addSubError(ApiSubError subError) {
            if (subErrors == null) {
                subErrors = new ArrayList<>();
            }
            subErrors.add(subError);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    static class ApiValidationError implements ApiSubError {
        private String object;
        private String field;
        private Object rejectedValue;
        private String message;
    }

    static class LowerCaseClassNameResolver extends TypeIdResolverBase {

        @Override
        public String idFromValue(Object value) {
            return value.getClass().getSimpleName().toLowerCase();
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return idFromValue(value);
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }
    }
}
