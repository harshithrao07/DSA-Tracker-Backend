package com.harshith.dsa_question_picker.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document(collection = "questions")
public class Question {
    @Id
    private UUID id;
    private String title;
    private String link;
    private Difficulty difficulty;
    private boolean solved;
    private boolean reviseLater;
    private List<UUID> topicIds;
    private UUID noteId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
