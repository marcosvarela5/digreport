package es.marcos.digreport.application.dto.authority;

public record TopReporterDto(
        Long userId,
        String userName,
        long totalFinds,
        long validatedFinds
) {}