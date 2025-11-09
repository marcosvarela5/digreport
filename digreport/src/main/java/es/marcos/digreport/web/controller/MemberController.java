package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.member.MemberRankingDto;
import es.marcos.digreport.application.dto.member.MemberStatsDto;
import es.marcos.digreport.application.port.in.MemberService;
import es.marcos.digreport.domain.model.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        List<MemberDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/stats")

    public ResponseEntity<List<MemberStatsDto>> getMembersWithStats() {
        List<MemberStatsDto> stats = memberService.getMembersWithStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<MemberDto>> getRanking(@RequestParam(defaultValue = "10") int limit) {
        List<MemberDto> ranking = memberService.getTopByReputation(limit).stream()
                .map(MemberDto::fromDomain)
                .toList();
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/ranking/public")
    public ResponseEntity<List<MemberRankingDto>> getPublicRanking(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<Member> topMembers = memberService.getTopByReputation(limit);

        // Convertir a DTO público (sin datos sensibles)
        List<MemberRankingDto> ranking = topMembers.stream()
                .map(member -> new MemberRankingDto(
                        member.getName() + " " + member.getSurname1(),
                        member.getReputation(),
                        member.getCcaa()
                ))
                .toList();

        return ResponseEntity.ok(ranking);
    }
}