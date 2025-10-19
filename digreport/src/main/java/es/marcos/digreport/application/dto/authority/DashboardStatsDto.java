package es.marcos.digreport.application.dto.authority;

import java.util.List;
import java.util.Map;

public record DashboardStatsDto(
        long totalFinds,
        long pendingFinds,
        long validatedFinds,
        long rejectedFinds,
        Map<String, Long> findsByStatus,
        List<TopReporterDto> topReporters,
        List<FindsByRegionDto> findsByCcaa,
        List<MonthlyStatsDto> monthlyStats
) {}