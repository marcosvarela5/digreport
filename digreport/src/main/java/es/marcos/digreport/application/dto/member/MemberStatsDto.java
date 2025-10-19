package es.marcos.digreport.application.dto.member;

public record MemberStatsDto(
        Long userId,
        String userName,
        String email,
        String role,
        int totalFinds,
        int validatedFinds,
        int rejectedFinds,
        int pendingFinds
) {}