package es.marcos.digreport.application.dto.member;

public record MemberRankingDto(
        String name,        // Solo nombre completo
        Integer reputation, // Puntos
        String ccaa
) {}