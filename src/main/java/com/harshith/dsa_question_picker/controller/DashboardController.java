package com.harshith.dsa_question_picker.controller;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.question.QuestionStatsCount;
import com.harshith.dsa_question_picker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats-count")
    public ResponseEntity<ApiResponseDTO<QuestionStatsCount>> getQuestionStatsCount() {
        return dashboardService.getQuestionStatsCount();
    }

    @GetMapping("/reset-progress")
    public ResponseEntity<ApiResponseDTO<Boolean>> resetProgress() {
        return dashboardService.resetProgress();
    }
}
