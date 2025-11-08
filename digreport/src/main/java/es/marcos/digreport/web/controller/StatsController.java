package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.stats.PublicStatsDto;
import es.marcos.digreport.application.port.in.FindService;
import es.marcos.digreport.application.port.in.MemberSelfService;
import es.marcos.digreport.application.port.in.MemberService;
import es.marcos.digreport.application.port.in.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/public")
    public ResponseEntity<PublicStatsDto> getPublicStats() {
        return ResponseEntity.ok(statsService.getPublicStats());
    }
}