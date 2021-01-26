package io.c12.bala.react.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto {

    private String id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String userId;
    private List<String> interests;
}
