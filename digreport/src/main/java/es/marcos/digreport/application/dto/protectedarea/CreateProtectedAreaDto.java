package es.marcos.digreport.application.dto.protectedarea;

import es.marcos.digreport.domain.enums.ProtectedAreaType;
import es.marcos.digreport.domain.enums.ProtectionType;

public record CreateProtectedAreaDto(
        String name,
        String description,
        ProtectedAreaType type,
        ProtectionType protectionType,
        String geometry,
        String ccaa
) {}