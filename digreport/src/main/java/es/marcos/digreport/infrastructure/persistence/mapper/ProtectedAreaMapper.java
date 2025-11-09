package es.marcos.digreport.infrastructure.persistence.mapper;

import es.marcos.digreport.domain.model.ProtectedArea;
import es.marcos.digreport.infrastructure.persistence.entities.ProtectedAreaEntityJpa;
import org.springframework.stereotype.Component;

@Component
public class ProtectedAreaMapper {

    public static ProtectedAreaEntityJpa toEntity(ProtectedArea area) {
        if (area == null) return null;
        return ProtectedAreaEntityJpa.builder()
                .id(area.getId())
                .name(area.getName())
                .description(area.getDescription())
                .type(area.getType())
                .protectionType(area.getProtectionType())
                .geometry(area.getGeometry())
                .ccaa(area.getCcaa())
                .color(area.getColor())
                .createdBy(area.getCreatedBy())
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .active(area.isActive())
                .build();
    }

    public static ProtectedArea toDomain(ProtectedAreaEntityJpa entity) {
        if (entity == null) return null;
        return new ProtectedArea(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.getProtectionType(),
                entity.getGeometry(),
                entity.getCcaa(),
                entity.getColor(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }
}