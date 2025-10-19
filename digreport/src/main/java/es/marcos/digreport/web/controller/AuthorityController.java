package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.authority.DashboardStatsDto;
import es.marcos.digreport.application.port.in.AuthorityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authority")
public class AuthorityController {

    private final AuthorityService authorityService;

    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        DashboardStatsDto stats = authorityService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}