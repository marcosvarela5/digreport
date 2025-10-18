package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.profile.UpdateProfileRequest;
import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.port.in.MemberSelfService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class MemberSelfController {

    private final MemberSelfService memberSelfService;

    public MemberSelfController(MemberSelfService memberSelfService) {
        this.memberSelfService = memberSelfService;
    }

    @PutMapping
    public ResponseEntity<MemberDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String currentEmail = authentication.getName();
        MemberDto updatedProfile = memberSelfService.updateProfile(currentEmail, request);
        return ResponseEntity.ok(updatedProfile);
    }
}