package es.marcos.digreport.application.dto.stats;

public record PublicStatsDto(
        Long totalFinds,
        Long totalArchaeologists,
        double validationRate,
        Long totalCitizens,
        Long pendingFinds
) {}