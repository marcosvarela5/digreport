package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.protectedarea.CreateProtectedAreaDto;
import es.marcos.digreport.application.dto.protectedarea.ProtectedAreaDto;
import es.marcos.digreport.application.port.in.MemberService;
import es.marcos.digreport.application.port.in.ProtectedAreaService;
import es.marcos.digreport.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/protected-areas")
@RequiredArgsConstructor
public class ProtectedAreaController {

    private final ProtectedAreaService protectedAreaService;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<List<ProtectedAreaDto>> getAllActive() {
        return ResponseEntity.ok(protectedAreaService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProtectedAreaDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(protectedAreaService.getById(id));
    }

    @GetMapping("/ccaa/{ccaa}")
    public ResponseEntity<List<ProtectedAreaDto>> getByCcaa(@PathVariable String ccaa) {
        return ResponseEntity.ok(protectedAreaService.getByCcaa(ccaa));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isProtected(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        return ResponseEntity.ok(
                protectedAreaService.isLocationProtected(latitude, longitude)
        );
    }

    // Endpoints solo para AUTHORITY (administradores)
    @PostMapping
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<ProtectedAreaDto> create(
            @RequestBody CreateProtectedAreaDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        MemberDto currentUser = memberService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(
                protectedAreaService.createProtectedArea(dto, currentUser.getId())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<ProtectedAreaDto> update(
            @PathVariable Long id,
            @RequestBody CreateProtectedAreaDto dto
    ) {
        return ResponseEntity.ok(protectedAreaService.updateProtectedArea(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        protectedAreaService.deleteProtectedArea(id);
        return ResponseEntity.noContent().build();
    }
}