package es.marcos.digreport.application.dto.authority;

public record FindsByRegionDto(
        String ccaa,
        long count
) {}