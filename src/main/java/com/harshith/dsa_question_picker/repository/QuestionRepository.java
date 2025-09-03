package com.harshith.dsa_question_picker.repository;

import com.harshith.dsa_question_picker.model.Difficulty;
import com.harshith.dsa_question_picker.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends MongoRepository<Question, UUID> {
    Optional<Question> findByTitle(String title);

    boolean existsByTitle(String title);

    long countBySolved(boolean solved);

    long countByDifficulty(Difficulty difficulty);

    long countByReviseLater(boolean reviseLater);
}
