package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.member.MemberStatsDto;
import es.marcos.digreport.application.port.in.MemberService;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.Member;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepositoryPort memberRepository;
    private final FindRepositoryPort findRepository;
    private final ResourcePatternResolver resourcePatternResolver;

    public MemberServiceImpl(MemberRepositoryPort memberRepository, FindRepositoryPort findRepository, ResourcePatternResolver resourcePatternResolver) {
        this.memberRepository = memberRepository;
        this.findRepository = findRepository;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberDto::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberStatsDto> getMembersWithStats() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(member -> {
                    List<Find> userFinds = findRepository.findByReporterId(member.getId());

                    int totalFinds = userFinds.size();
                    long validatedFinds = userFinds.stream()
                            .filter(find -> find.getStatus() == FindValidationStatus.VALIDATED)
                            .count();
                    long rejectedFinds = userFinds.stream()
                            .filter(find -> find.getStatus() == FindValidationStatus.REJECTED)
                            .count();
                    long pendingFinds = userFinds.stream()
                            .filter(find -> find.getStatus() == FindValidationStatus.PENDING)
                            .count();

                    return new MemberStatsDto(
                            member.getId(),
                            member.getName() + " " + member.getSurname1(),
                            member.getEmail(),
                            member.getRole().name(),
                            totalFinds,
                            (int) validatedFinds,
                            (int) rejectedFinds,
                            (int) pendingFinds
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MemberDto> findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberDto::fromDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getTopByReputation(int limit) {
        return memberRepository.findAllByReputation(limit);
    }

    @Override
    @Transactional
    public void incrementReputation(Long memberId, int points) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setReputation(member.getReputation() + points);
        memberRepository.save(member);
    }


}