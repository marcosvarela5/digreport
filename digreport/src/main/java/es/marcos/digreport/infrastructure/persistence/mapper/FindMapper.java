package es.marcos.digreport.infrastructure.persistence.mapper;

import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.infrastructure.persistence.entities.FindEntityJpa;
import org.springframework.stereotype.Component;

@Component
public class FindMapper {

    public static FindEntityJpa toEntity(Find find) {
        if (find == null) return null;
        return FindEntityJpa.builder()
                .id(find.getId())
                .discoveredAt(find.getDiscoveredAt())
                .latitude(find.getLatitude())
                .longitude(find.getLongitude())
                .reporterId(find.getReporterId())
                .description(find.getDescription())
                .status(find.getStatus())
                .findPriority(find.getFindPriority())
                .ccaa(find.getCcaa())
                .validatedBy(find.getValidatedBy())
                .build();
    }

    public static Find toDomain(FindEntityJpa entity) {
        if (entity == null) return null;

        Find find = new Find(
                entity.getId(),
                entity.getDiscoveredAt(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getReporterId(),
                entity.getDescription(),
                entity.getCcaa(),
                entity.getValidatedBy()
        );

        find.setStatus(entity.getStatus());
        find.setFindPriority(entity.getFindPriority());

        return find;
    }
}