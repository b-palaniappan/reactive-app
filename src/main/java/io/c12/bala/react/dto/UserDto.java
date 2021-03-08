package io.c12.bala.react.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto {

    private String id;

    @NotNull(message = "firstName is required")
    private String firstName;

    @NotNull(message = "lastName is required")
    private String lastName;

    @NotNull(message = "emailId is required")
    @Email(message = "invalid emailId")
    private String emailId;

    @NotNull(message = "user id is required")
    private String userId;
    private List<String> interests;
}
