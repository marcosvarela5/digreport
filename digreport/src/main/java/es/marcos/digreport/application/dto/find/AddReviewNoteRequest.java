package es.marcos.digreport.application.dto.find;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddReviewNoteRequest(
        @NotBlank(message = "El comentario no puede estar vacío")
        @Size(max = 2000, message = "El comentario no puede exceder 2000 caracteres")
        String comment
) {}