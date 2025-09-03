package com.harshith.dsa_question_picker.service;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.note.NoteResponseDTO;
import com.harshith.dsa_question_picker.dto.note.PostNoteDTO;
import com.harshith.dsa_question_picker.dto.note.UpdateNote;
import com.harshith.dsa_question_picker.model.Note;
import com.harshith.dsa_question_picker.model.Question;
import com.harshith.dsa_question_picker.repository.NoteRepository;
import com.harshith.dsa_question_picker.repository.QuestionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.harshith.dsa_question_picker.utils.Utility.objectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotesService {
    private final NoteRepository noteRepository;
    private final QuestionRepository questionRepository;

    public ResponseEntity<ApiResponseDTO<UUID>> addNote(@Valid PostNoteDTO postNoteDTO) {
        try {
            Question question = questionRepository.findById(postNoteDTO.questionId()).orElse(null);
            if (question == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(false, "Question with id:" + postNoteDTO.questionId() + " does not exist", null));
            }

            Note note = Note.builder()
                    .id(UUID.randomUUID())
                    .questionId(postNoteDTO.questionId())
                    .text(postNoteDTO.text())
                    .build();

            note = noteRepository.save(note);

            question.setNoteId(note.getId());
            questionRepository.save(question);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, note.getId()));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    public ResponseEntity<ApiResponseDTO<NoteResponseDTO>> getNote(String noteId) {
        try {
            Note note = noteRepository.findById(UUID.fromString(noteId)).orElse(null);
            if (note == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(false, "Note with id:" + noteId + " does not exist", null));
            }

            NoteResponseDTO noteResponseDTO = objectMapper.convertValue(note, NoteResponseDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, noteResponseDTO));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }

    public ResponseEntity<ApiResponseDTO<UUID>> updateNote(@Valid UpdateNote updateNote, String noteId) {
        try {
            Note note = noteRepository.findById(UUID.fromString(noteId)).orElse(null);
            if (note == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(false, "Note with id:" + noteId + " does not exist", null));
            }

            note.setText(updateNote.text());
            note = noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO<>(true, null, note.getId()));
        } catch (Exception e) {
            log.error("An exception has occurred {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO<>(false, "An error occurred in the server", null));
        }
    }
}
