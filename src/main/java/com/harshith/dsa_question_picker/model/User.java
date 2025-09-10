package com.harshith.dsa_question_picker.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Document(collection = "users")
public class User {
    @Id
    private UUID id;

    private String email;
    private String name;
    private String pictureUrl;

    private String provider;
    private String providerId;

    @CreatedDate
    private Instant createdAt;
}
