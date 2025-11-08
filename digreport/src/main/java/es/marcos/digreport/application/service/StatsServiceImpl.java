package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.stats.PublicStatsDto;
import es.marcos.digreport.application.port.in.FindService;
import es.marcos.digreport.application.port.in.StatsService;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private final MemberRepositoryPort memberRepositoryPort;
    private final FindRepositoryPort findRepositoryPort;

    public StatsServiceImpl(MemberRepositoryPort memberRepositoryPort, FindRepositoryPort findRepositoryPort) {
        this.memberRepositoryPort = memberRepositoryPort;
        this.findRepositoryPort = findRepositoryPort;
    }

    @Override
    public PublicStatsDto getPublicStats() {
        Long totalFinds = findRepositoryPort.count();
        Long pendingFinds = findRepositoryPort.countByStatus(FindValidationStatus.PENDING);
        Long validatedFinds = findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED);
        Long totalUsers = memberRepositoryPort.count();
        Long archaeologists = memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST);

        double validationRate;
        if (totalFinds > 0) {
            validationRate = (validatedFinds * 100.0) / totalFinds;
        } else validationRate = 100.0;

        return new PublicStatsDto(
                totalFinds,
                archaeologists,
                validationRate,
                totalUsers,
                pendingFinds
        );
    }
}
