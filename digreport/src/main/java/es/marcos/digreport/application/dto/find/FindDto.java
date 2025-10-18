package es.marcos.digreport.application.dto.find;

import es.marcos.digreport.domain.enums.FindPriority;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import java.time.LocalDateTime;

public record FindDto(
        Long id,
        LocalDateTime discoveredAt,
        Double latitude,
        Double longitude,
        String description,
        FindValidationStatus status,
        FindPriority priority,
        Long reporterId,
        String reporterName,
        LocalDateTime createdAt
) {}