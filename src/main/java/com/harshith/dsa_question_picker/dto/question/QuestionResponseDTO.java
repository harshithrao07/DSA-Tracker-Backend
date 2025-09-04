package com.harshith.dsa_question_picker.dto.question;

import com.harshith.dsa_question_picker.model.Difficulty;

import java.util.List;
import java.util.UUID;

public record QuestionResponseDTO(
        UUID id,
        String link,
        String title,
        Difficulty difficulty,
        boolean solved,
        boolean reviseLater,
        List<String> topics,
        UUID noteId
) {
}
