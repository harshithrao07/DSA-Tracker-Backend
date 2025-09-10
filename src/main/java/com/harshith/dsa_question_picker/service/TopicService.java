package com.harshith.dsa_question_picker.service;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.topic.PostTopicDTO;
import com.harshith.dsa_question_picker.dto.topic.TopicResponseDTO;
import com.harshith.dsa_question_picker.model.Topic;
import com.harshith.dsa_question_picker.repository.TopicRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.harshith.dsa_question_picker.utils.Utility.objectMapper;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public ResponseEntity<ApiResponseDTO<List<TopicResponseDTO>>> getAllTopics(UUID createdBy) {
        try {
            List<Topic> topics = topicRepository.findByCreatedBy(createdBy);
            List<TopicResponseDTO> topicResponseDTOS = topics.stream()
                    .map(topic -> objectMapper.convertValue(topic, TopicResponseDTO.class))
                    .toList();

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, topicResponseDTOS));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    public ResponseEntity<ApiResponseDTO<TopicResponseDTO>> postTopic(@Valid PostTopicDTO postTopicDTO, UUID createdBy) {
        try {
            if (topicRepository.existsByNameAndCreatedBy(postTopicDTO.name(), createdBy)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO<>(false, "Topic with the same name already exists", null));
            }
            Topic topic = Topic.builder()
                    .id(UUID.randomUUID())
                    .name(postTopicDTO.name())
                    .createdBy(createdBy)
                    .build();

            topic = topicRepository.save(topic);
            TopicResponseDTO topicResponseDTO = objectMapper.convertValue(topic, TopicResponseDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, topicResponseDTO));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }
}
