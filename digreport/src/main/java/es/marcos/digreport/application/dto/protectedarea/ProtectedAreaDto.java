package es.marcos.digreport.application.dto.protectedarea;

import es.marcos.digreport.domain.enums.ProtectedAreaType;
import es.marcos.digreport.domain.enums.ProtectionType;

import java.time.LocalDateTime;

public record ProtectedAreaDto(
        Long id,
        String name,
        String description,
        ProtectedAreaType type,
        ProtectionType protectionType,
        String geometry,
        String ccaa,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {}