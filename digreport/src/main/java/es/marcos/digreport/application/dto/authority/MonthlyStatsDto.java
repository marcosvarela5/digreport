package es.marcos.digreport.application.dto.authority;

public record MonthlyStatsDto(
        String month,
        long count
) {}