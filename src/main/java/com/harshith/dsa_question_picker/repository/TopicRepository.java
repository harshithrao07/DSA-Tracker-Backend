package com.harshith.dsa_question_picker.repository;

import com.harshith.dsa_question_picker.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends MongoRepository<Topic, UUID> {
    boolean existsByName(String name);

    Optional<Topic> findByName(String name);
}
