package com.harshith.dsa_question_picker.service.autofill;

import com.harshith.dsa_question_picker.dto.question.QuestionAutofillDTO;
import com.harshith.dsa_question_picker.model.Difficulty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class CodeforcesFetcher {

    private static final String CF_API = "https://codeforces.com/api/problemset.problems";
    private final WebClient webClient = WebClient.create();

    public QuestionAutofillDTO fetch(String link) {
        try {
            // Example link: https://codeforces.com/problemset/problem/4/A
            String[] parts = link.split("/");
            String contestId = parts[parts.length - 2]; // "4"
            String index = parts[parts.length - 1];     // "A"

            // Fetch full problemset (not ideal, but Codeforces API doesn’t allow single-problem fetch)
            Map<String, Object> response = webClient.get()
                    .uri(CF_API)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !"OK".equals(response.get("status"))) {
                return null;
            }

            Map<String, Object> result = (Map<String, Object>) response.get("result");
            List<Map<String, Object>> problems = (List<Map<String, Object>>) result.get("problems");

            // Find the specific problem
            for (Map<String, Object> problem : problems) {
                if (String.valueOf(problem.get("contestId")).equals(contestId)
                        && problem.get("index").equals(index)) {

                    String title = (String) problem.get("name");
                    String difficulty = "Unknown";
                    if (problem.containsKey("rating")) {
                        int rating = (Integer) problem.get("rating");
                        if (rating <= 1200) {
                            difficulty = Difficulty.EASY.name();
                        } else if (rating <= 2000) {
                            difficulty = Difficulty.MEDIUM.name();
                        } else {
                            difficulty = Difficulty.HARD.name();
                        }
                    }

                    List<String> topics = (List<String>) problem.get("tags");

                    return new QuestionAutofillDTO("CODEFORCES", title, topics, Difficulty.valueOf(difficulty));
                }
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
