package com.harshith.dsa_question_picker.repository;

import com.harshith.dsa_question_picker.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface NoteRepository extends MongoRepository<Note, UUID> {
}
