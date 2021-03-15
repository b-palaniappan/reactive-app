package io.c12.bala.react.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Document("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class User {

    @MongoId
    private String id;

    private String firstName;
    private String lastName;
    private String emailId;
    private String userId;
    private List<String> interests = new ArrayList<>();
}
