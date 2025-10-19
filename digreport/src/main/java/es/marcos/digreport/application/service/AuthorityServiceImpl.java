package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.authority.*;
import es.marcos.digreport.application.port.in.AuthorityService;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final FindRepositoryPort findRepository;
    private final MemberRepositoryPort memberRepository;

    public AuthorityServiceImpl(FindRepositoryPort findRepository, MemberRepositoryPort memberRepository) {
        this.findRepository = findRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        List<Find> allFinds = findRepository.findAll();

        long total = allFinds.size();
        long pending = allFinds.stream().filter(f -> f.getStatus() == FindValidationStatus.PENDING).count();
        long validated = allFinds.stream().filter(f -> f.getStatus() == FindValidationStatus.VALIDATED).count();
        long rejected = allFinds.stream().filter(f -> f.getStatus() == FindValidationStatus.REJECTED).count();

        Map<String, Long> byStatus = allFinds.stream()
                .collect(Collectors.groupingBy(f -> f.getStatus().name(), Collectors.counting()));

        List<TopReporterDto> topReporters = calculateTopReporters(allFinds);
        List<FindsByRegionDto> byCcaa = calculateFindsByCcaa(allFinds);
        List<MonthlyStatsDto> monthlyStats = calculateMonthlyStats(allFinds);

        return new DashboardStatsDto(
                total,
                pending,
                validated,
                rejected,
                byStatus,
                topReporters,
                byCcaa,
                monthlyStats
        );
    }

    private List<TopReporterDto> calculateTopReporters(List<Find> finds) {
        Map<Long, List<Find>> byReporter = finds.stream()
                .collect(Collectors.groupingBy(Find::getReporterId));

        return byReporter.entrySet().stream()
                .map(entry -> {
                    Long reporterId = entry.getKey();
                    List<Find> reporterFinds = entry.getValue();
                    Member member = memberRepository.findById(reporterId).orElse(null);

                    if (member == null) return null;

                    long validatedCount = reporterFinds.stream()
                            .filter(f -> f.getStatus() == FindValidationStatus.VALIDATED)
                            .count();

                    return new TopReporterDto(
                            reporterId,
                            member.getName() + " " + member.getSurname1(),
                            reporterFinds.size(),
                            validatedCount
                    );
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(TopReporterDto::totalFinds).reversed())
                .limit(10)
                .toList();
    }

    private List<FindsByRegionDto> calculateFindsByCcaa(List<Find> finds) {
        Map<String, Long> byCcaa = finds.stream()
                .filter(f -> f.getCcaa() != null && !f.getCcaa().isBlank())
                .collect(Collectors.groupingBy(Find::getCcaa, Collectors.counting()));

        return byCcaa.entrySet().stream()
                .map(e -> new FindsByRegionDto(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(FindsByRegionDto::count).reversed())
                .toList();
    }

    private List<MonthlyStatsDto> calculateMonthlyStats(List<Find> finds) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        Map<String, Long> byMonth = finds.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getDiscoveredAt().format(formatter),
                        Collectors.counting()
                ));

        return byMonth.entrySet().stream()
                .map(e -> new MonthlyStatsDto(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(MonthlyStatsDto::month))
                .toList();
    }
}