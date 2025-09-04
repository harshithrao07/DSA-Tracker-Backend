package com.harshith.dsa_question_picker.service;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.question.AutofillRequestDTO;
import com.harshith.dsa_question_picker.dto.question.QuestionAutofillDTO;
import com.harshith.dsa_question_picker.service.autofill.CodeforcesFetcher;
import com.harshith.dsa_question_picker.service.autofill.GeeksForGeeksFetcher;
import com.harshith.dsa_question_picker.service.autofill.LeetCodeFetcher;
import com.harshith.dsa_question_picker.utils.Utility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionAutofillService {
    private final LeetCodeFetcher leetCodeFetcher;
    private final CodeforcesFetcher codeforcesFetcher;
    private final GeeksForGeeksFetcher geeksForGeeksFetcher;

    public ResponseEntity<ApiResponseDTO<QuestionAutofillDTO>> autofillQuestion(@Valid AutofillRequestDTO autofillRequestDTO) {
        try {
            String platform = detectPlatform(autofillRequestDTO.link());

            if (platform == null || platform.equals("UNKNOWN")) {
                return ResponseEntity.badRequest().body(
                        new ApiResponseDTO<>(false, "Unsupported or invalid platform", null)
                );
            }

            QuestionAutofillDTO questionData = switch (platform) {
                case "LEETCODE" -> leetCodeFetcher.fetch(autofillRequestDTO.link());
                case "CODEFORCES" -> codeforcesFetcher.fetch(autofillRequestDTO.link());
                case "GEEKSFORGEEKS" -> geeksForGeeksFetcher.fetch(autofillRequestDTO.link());
                default -> null;
            };


            if (questionData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ApiResponseDTO<>(false, "Could not fetch problem details", null)
                );
            }

            return ResponseEntity.ok(
                    new ApiResponseDTO<>(true, null, questionData)
            );
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    private String detectPlatform(String link) {
        try {
            if (!Utility.isValidUrl(link)) {
                return null;
            }

            URL url = new URL(link);
            String host = url.getHost().toLowerCase();

            if (host.contains("leetcode.com")) {
                return "LEETCODE";
            } else if (host.contains("codeforces.com")) {
                return "CODEFORCES";
            } else if (host.contains("geeksforgeeks.org")) {
                return "GEEKSFORGEEKS";
            } else {
                return "UNKNOWN";
            }
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return null;
        }
    }

}
