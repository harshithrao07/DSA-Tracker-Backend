package com.harshith.dsa_question_picker.controller;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.topic.PostTopicDTO;
import com.harshith.dsa_question_picker.dto.topic.TopicResponseDTO;
import com.harshith.dsa_question_picker.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TopicResponseDTO>>> getAllTopics() {
        return topicService.getAllTopics();
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<TopicResponseDTO>> postTopic(@Valid @RequestBody PostTopicDTO postTopicDTO) {
        return topicService.postTopic(postTopicDTO);
    }

}
