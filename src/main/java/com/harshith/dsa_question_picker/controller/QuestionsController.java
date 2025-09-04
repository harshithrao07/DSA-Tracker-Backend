package com.harshith.dsa_question_picker.controller;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.question.*;
import com.harshith.dsa_question_picker.model.Difficulty;
import com.harshith.dsa_question_picker.service.QuestionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionsController {
    private final QuestionsService questionsService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<AllQuestionsDTO>> getAllQuestions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "topics", required = false) List<String> topics,
            @RequestParam(name = "difficulty", required = false) String difficulty,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        return questionsService.getAllQuestion(
                page,
                pageSize,
                key,
                topics,
                difficulty,
                status,
                sortBy,
                sortDir
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<QuestionResponseDTO>> addQuestion(@Valid @RequestBody PostQuestionDTO postQuestionDTO) {
        return questionsService.addQuestion(postQuestionDTO);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<ApiResponseDTO<QuestionResponseDTO>> updateQuestion(@Valid @RequestBody UpdateQuestionDTO updateQuestionDTO, @PathVariable("questionId") String questionId) {
        return questionsService.updateQuestion(updateQuestionDTO, questionId);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<ApiResponseDTO<Boolean>> deleteQuestion(@PathVariable("questionId") String questionId) {
        return questionsService.deleteQuestion(questionId);
    }

    @GetMapping("/stats-count")
    public ResponseEntity<ApiResponseDTO<QuestionStatsCount>> getQuestionStatsCount() {
        return questionsService.getQuestionStatsCount();
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponseDTO<List<QuestionResponseDTO>>> getRandomQuestions(
            @RequestParam(name = "topics", required = false) List<String> topics,
            @RequestParam(name = "difficulty", required = false) Difficulty difficulty,
            @RequestParam(name = "revision", defaultValue = "false") boolean markedForRevision,
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        return questionsService.getRandomQuestions(topics, difficulty, markedForRevision, count);
    }
}
