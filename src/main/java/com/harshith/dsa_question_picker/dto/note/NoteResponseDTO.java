package com.harshith.dsa_question_picker.dto.note;

import java.time.Instant;
import java.util.UUID;

public record NoteResponseDTO(
        UUID id,
        UUID questionId,
        String text,
        Instant createdAt,
        Instant updatedAt
) {
}
