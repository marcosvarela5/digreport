package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.find.CreateFindRequest;
import es.marcos.digreport.application.dto.find.FindDto;
import es.marcos.digreport.application.dto.find.ValidateFindRequest;
import es.marcos.digreport.application.port.in.FindService;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FindServiceImpl implements FindService {

    private final FindRepositoryPort findRepository;
    private final MemberRepositoryPort memberRepository;

    public FindServiceImpl(FindRepositoryPort findRepository, MemberRepositoryPort memberRepository) {
        this.findRepository = findRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public FindDto createFind(String reporterEmail, CreateFindRequest request) {
        Member reporter = memberRepository.findByEmail(reporterEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (reporter.getRole() != UserRole.USER) {
            throw new RuntimeException("Solo los ciudadanos pueden registrar hallazgos");
        }

        Find find = new Find();
        find.setDiscoveredAt(request.discoveredAt());
        find.setLatitude(request.latitude());
        find.setLongitude(request.longitude());
        find.setDescription(request.description());
        find.setReporterId(reporter.getId());
        find.setStatus(FindValidationStatus.PENDING);

        Find saved = findRepository.save(find);
        return toDto(saved, reporter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getMyFinds(String reporterEmail) {
        Member reporter = memberRepository.findByEmail(reporterEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return findRepository.findByReporterId(reporter.getId())
                .stream()
                .map(find -> toDto(find, reporter))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FindDto> getFindById(Long id, String userEmail) {
        Member user = memberRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return findRepository.findById(id)
                .filter(find -> canAccessFind(find, user))
                .map(find -> {
                    Member reporter = memberRepository.findById(find.getReporterId())
                            .orElseThrow(() -> new RuntimeException("Reportero no encontrado"));
                    return toDto(find, reporter);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getPendingFinds() {
        return findRepository.findByStatus(FindValidationStatus.PENDING)
                .stream()
                .map(this::toDtoWithReporter)
                .toList();
    }

    @Override
    @Transactional
    public FindDto validateFind(Long findId, String archaeologistEmail, ValidateFindRequest request) {
        Member archaeologist = memberRepository.findByEmail(archaeologistEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (archaeologist.getRole() != UserRole.ARCHAEOLOGIST) {
            throw new RuntimeException("Solo los arqueólogos pueden validar hallazgos");
        }

        Find find = findRepository.findById(findId)
                .orElseThrow(() -> new RuntimeException("Hallazgo no encontrado"));

        if (find.getStatus() != FindValidationStatus.PENDING) {
            throw new RuntimeException("El hallazgo ya ha sido procesado");
        }

        find.setStatus(request.status());
        if (request.priority() != null) {
            find.setFindPriority(request.priority());
        }

        Find saved = findRepository.save(find);

        Member reporter = memberRepository.findById(saved.getReporterId())
                .orElseThrow(() -> new RuntimeException("Reportero no encontrado"));

        return toDto(saved, reporter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getAllFinds() {
        return findRepository.findAll()
                .stream()
                .map(this::toDtoWithReporter)
                .toList();
    }

    private boolean canAccessFind(Find find, Member user) {
        return switch (user.getRole()) {
            case USER -> find.getReporterId().equals(user.getId());
            case ARCHAEOLOGIST, AUTHORITY -> true;
        };
    }

    private FindDto toDto(Find find, Member reporter) {
        return new FindDto(
                find.getId(),
                find.getDiscoveredAt(),
                find.getLatitude(),
                find.getLongitude(),
                find.getDescription(),
                find.getStatus(),
                find.getFindPriority(),
                reporter.getId(),
                reporter.getName() + " " + reporter.getSurname1(),
                LocalDateTime.now()
        );
    }

    private FindDto toDtoWithReporter(Find find) {
        Member reporter = memberRepository.findById(find.getReporterId())
                .orElseThrow(() -> new RuntimeException("Reportero no encontrado"));
        return toDto(find, reporter);
    }
}