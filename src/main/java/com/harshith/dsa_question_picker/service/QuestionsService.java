package com.harshith.dsa_question_picker.service;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.question.PostQuestionDTO;
import com.harshith.dsa_question_picker.dto.question.QuestionResponseDTO;
import com.harshith.dsa_question_picker.dto.question.QuestionStatsCount;
import com.harshith.dsa_question_picker.dto.question.UpdateQuestionDTO;
import com.harshith.dsa_question_picker.model.Note;
import com.harshith.dsa_question_picker.model.Question;
import com.harshith.dsa_question_picker.model.Topic;
import com.harshith.dsa_question_picker.repository.NoteRepository;
import com.harshith.dsa_question_picker.repository.QuestionRepository;
import com.harshith.dsa_question_picker.repository.TopicRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.harshith.dsa_question_picker.utils.Utility.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionsService {
    private final QuestionRepository questionRepository;
    private final NoteRepository noteRepository;
    private final TopicRepository topicRepository;

    public ResponseEntity<ApiResponseDTO<List<QuestionResponseDTO>>> getAllQuestion(int page, int pageSize, String key, List<String> topics, String difficulty, String status, String sortBy, String sortDir) {
        try {
            Sort sort = Sort.by(sortBy);
            sort = sortDir.equals("desc") ? sort.descending() : sort.ascending();
            Pageable pageable = PageRequest.of(page, pageSize, sort);

            Query query = new Query().with(pageable);

            if (key != null && !key.isBlank()) {
                query.addCriteria(Criteria.where("title").regex(key, "i")); // "i" is the case-insensitive flag
            }

            if (topics != null && !topics.isEmpty()) {
                topics.forEach(topic -> {
                    if (topicRepository.existsByName(topic)) {
                        Optional<Topic> topicOptional = topicRepository.findByName(topic);
                        topicOptional.ifPresent(value -> query.addCriteria(Criteria.where("topics").in(value.getId())));
                    }
                });
            }

            if (difficulty != null) {
                query.addCriteria(Criteria.where("difficulty").is(difficulty));
            }

            if (status != null && !status.isBlank()) {
                if (status.equals("reviseLater")) {
                    query.addCriteria(Criteria.where("reviseLater").is(true));
                }

                if (status.equals("solved")) {
                    query.addCriteria(Criteria.where("solved").is(true));
                }

                if (status.equals("notSolved")) {
                    query.addCriteria(Criteria.where("solved").is(false));
                }
            }

            List<Question> questions = mongoTemplate.find(query, Question.class);

            List<QuestionResponseDTO> questionResponseDTOS = questions.stream()
                    .map(question -> objectMapper.convertValue(question, QuestionResponseDTO.class))
                    .toList();

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, questionResponseDTOS));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    public ResponseEntity<ApiResponseDTO<QuestionResponseDTO>> addQuestion(@Valid PostQuestionDTO postQuestionDTO) {
        try {
            if (questionRepository.existsByTitle(postQuestionDTO.title())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponseDTO<>(false, "Problem with the same title already exists", null));
            }

            if (isValidUrl(postQuestionDTO.link())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(false, "Problem link is invalid", null));
            }

            Question question = objectMapper.convertValue(postQuestionDTO, Question.class);
            question.setId(UUID.randomUUID());
            question = questionRepository.save(question);

            if (postQuestionDTO.note() != null && !postQuestionDTO.note().isBlank()) {
                Note note = Note.builder()
                        .id(UUID.randomUUID())
                        .questionId(question.getId())
                        .text(postQuestionDTO.note())
                        .build();

                Note savedNote = noteRepository.save(note);

                question.setNoteId(savedNote.getId());
                question = questionRepository.save(question);
            }

            QuestionResponseDTO questionResponseDTO = objectMapper.convertValue(question, QuestionResponseDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, questionResponseDTO));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    public ResponseEntity<ApiResponseDTO<QuestionResponseDTO>> updateQuestion(@Valid UpdateQuestionDTO updateQuestionDTO, String questionId) {
        try {
            Optional<Question> question = questionRepository.findById(UUID.fromString(questionId));
            if (question.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(false, "Question with id:" + questionId + " does not exist", null));
            }

            if (updateQuestionDTO.title() != null && !updateQuestionDTO.title().isBlank()) {
                question.get().setTitle(updateQuestionDTO.title());
            }

            if (updateQuestionDTO.link() != null && !updateQuestionDTO.link().isBlank()) {
                if (isValidUrl(updateQuestionDTO.link())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(false, "Problem link is invalid", null));
                }

                question.get().setLink(updateQuestionDTO.link());
            }

            if (updateQuestionDTO.reviseLater() != null) {
                question.get().setReviseLater(updateQuestionDTO.reviseLater());
            }

            if (updateQuestionDTO.solved() != null) {
                question.get().setReviseLater(updateQuestionDTO.solved());
            }

            if (updateQuestionDTO.difficulty() != null) {
                question.get().setDifficulty(updateQuestionDTO.difficulty());
            }

            if (updateQuestionDTO.topicIds() != null && !updateQuestionDTO.topicIds().isEmpty()) {
                for (UUID topicId : updateQuestionDTO.topicIds()) {
                    if (!topicRepository.existsById(topicId)) {
                        throw new IllegalArgumentException("Topic with id " + topicId + " does not exist");
                    }
                }
                question.get().setTopicIds(updateQuestionDTO.topicIds());
            }

            Question updatedQuestion = questionRepository.save(question.get());
            QuestionResponseDTO questionResponseDTO = objectMapper.convertValue(updatedQuestion, QuestionResponseDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, questionResponseDTO));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    public ResponseEntity<ApiResponseDTO<Boolean>> deleteQuestion(String questionId) {
        try {
            UUID id = UUID.fromString(questionId);
            if (questionRepository.existsById(id)) {
                questionRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, true));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(false, "Question of id:" + questionId + " does not exist", false));
            }
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", false));
        }
    }

    public ResponseEntity<ApiResponseDTO<QuestionStatsCount>> getQuestionStatsCount() {
        try {
            long totalQuestions = questionRepository.count();

        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", false));
        }
    }
}
