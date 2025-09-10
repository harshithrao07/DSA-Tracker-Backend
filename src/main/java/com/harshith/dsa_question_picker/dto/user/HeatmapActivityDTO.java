package com.harshith.dsa_question_picker.dto.user;

import java.time.Instant;

public record HeatmapActivityDTO(
        Instant date,
        long count
) {
}
