package com.harshith.dsa_question_picker.service.autofill;

import com.harshith.dsa_question_picker.dto.question.QuestionAutofillDTO;
import com.harshith.dsa_question_picker.model.Difficulty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class LeetCodeFetcher {

    private static final String LEETCODE_GRAPHQL = "https://leetcode.com/graphql";
    private final WebClient webClient = WebClient.create();

    public QuestionAutofillDTO fetch(String link) {
        try {
            // Extract slug from URL: https://leetcode.com/problems/two-sum/ -> "two-sum"
            String[] parts = link.split("/");
            String slug = parts[parts.length - 1].isBlank()
                    ? parts[parts.length - 2]
                    : parts[parts.length - 1];

            String query = """
                        query getQuestionDetail($titleSlug: String!) {
                          question(titleSlug: $titleSlug) {
                            title
                            difficulty
                            topicTags { name }
                          }
                        }
                    """;

            Map<String, Object> response = webClient.post()
                    .uri(LEETCODE_GRAPHQL)
                    .bodyValue(Map.of(
                            "query", query,
                            "variables", Map.of("titleSlug", slug)
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("data") == null) {
                return null;
            }

            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("question");

            String title = (String) data.get("title");
            String rawDifficulty = (String) data.get("difficulty");
            Difficulty difficulty = switch (rawDifficulty.toUpperCase()) {
                case "EASY" -> Difficulty.EASY;
                case "MEDIUM" -> Difficulty.MEDIUM;
                case "HARD" -> Difficulty.HARD;
                default -> Difficulty.MEDIUM; // fallback
            };

            List<Map<String, String>> tags = (List<Map<String, String>>) data.get("topicTags");
            List<String> topics = tags.stream().map(tag -> tag.get("name")).toList();

            return new QuestionAutofillDTO("LEETCODE", title, topics, difficulty);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
