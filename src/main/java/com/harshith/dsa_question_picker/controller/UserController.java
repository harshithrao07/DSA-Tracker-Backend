package com.harshith.dsa_question_picker.controller;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.user.HeatmapActivityDTO;
import com.harshith.dsa_question_picker.dto.question.QuestionStatsCount;
import com.harshith.dsa_question_picker.security.CustomUserPrinciple;
import com.harshith.dsa_question_picker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final DashboardService dashboardService;

    @GetMapping("/me")
    public Object getCurrentUserDetails(@AuthenticationPrincipal CustomUserPrinciple oAuth2User) {
        return oAuth2User.getUser();
    }

    @GetMapping("/stats-count")
    public ResponseEntity<ApiResponseDTO<QuestionStatsCount>> getQuestionStatsCount(@AuthenticationPrincipal CustomUserPrinciple oAuth2User) {
        UUID createdBy = oAuth2User.getUser().getId();
        return dashboardService.getQuestionStatsCount(createdBy);
    }

    @GetMapping("/reset-progress")
    public ResponseEntity<ApiResponseDTO<Boolean>> resetProgress(@AuthenticationPrincipal CustomUserPrinciple oAuth2User) {
        UUID createdBy = oAuth2User.getUser().getId();
        return dashboardService.resetProgress(createdBy);
    }

    @GetMapping("/heatmap-activities")
    public ResponseEntity<ApiResponseDTO<List<HeatmapActivityDTO>>> getHeatmapActivities(@AuthenticationPrincipal CustomUserPrinciple oAuth2User) {
        UUID createdBy = oAuth2User.getUser().getId();
        return dashboardService.getHeatmapActivities(createdBy);
    }

}
