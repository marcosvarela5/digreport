package es.marcos.digreport.application.dto.find;

import java.time.LocalDateTime;

public record ReviewNoteDto(
        Long id,
        Long findId,
        Long reviewerId,
        String reviewerName,
        String comment,
        LocalDateTime createdAt
) {}