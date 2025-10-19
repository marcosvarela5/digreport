package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.member.MemberStatsDto;
import es.marcos.digreport.application.port.in.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<List<MemberStatsDto>> getMembersWithStats() {
        List<MemberStatsDto> stats = memberService.getMembersWithStats();
        return ResponseEntity.ok(stats);
    }
}