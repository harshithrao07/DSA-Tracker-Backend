package com.harshith.dsa_question_picker.controller;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.topic.PostTopicDTO;
import com.harshith.dsa_question_picker.dto.topic.TopicResponseDTO;
import com.harshith.dsa_question_picker.security.CustomUserPrinciple;
import com.harshith.dsa_question_picker.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TopicResponseDTO>>> getAllTopics(@AuthenticationPrincipal CustomUserPrinciple oAuth2User) {
        UUID createdBy = oAuth2User.getUser().getId();
        return topicService.getAllTopics(createdBy);
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<TopicResponseDTO>> postTopic(@Valid @RequestBody PostTopicDTO postTopicDTO, @AuthenticationPrincipal CustomUserPrinciple oAuth2User) {
        UUID createdBy = oAuth2User.getUser().getId();
        return topicService.postTopic(postTopicDTO);
    }

}
