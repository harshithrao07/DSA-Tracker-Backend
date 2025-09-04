package com.harshith.dsa_question_picker.controller;

import com.harshith.dsa_question_picker.dto.ApiResponseDTO;
import com.harshith.dsa_question_picker.dto.note.NoteResponseDTO;
import com.harshith.dsa_question_picker.dto.note.PostNoteDTO;
import com.harshith.dsa_question_picker.dto.note.UpdateNote;
import com.harshith.dsa_question_picker.service.NotesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {
    private final NotesService notesService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<UUID>> addNote(@Valid @RequestBody PostNoteDTO postNoteDTO) {
        return notesService.addNote(postNoteDTO);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponseDTO<NoteResponseDTO>> getNote(@PathVariable("noteId") String noteId) {
        return notesService.getNote(noteId);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponseDTO<UUID>> updateNote(@Valid @RequestBody UpdateNote updateNote, @PathVariable("noteId") String noteId) {
        return notesService.updateNote(updateNote, noteId);
    }
}
