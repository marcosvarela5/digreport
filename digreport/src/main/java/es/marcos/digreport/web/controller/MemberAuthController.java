package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.auth.LoginRequest;
import es.marcos.digreport.application.dto.auth.LoginResponse;
import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.port.in.MemberAuthService;
import es.marcos.digreport.application.port.in.command.UserRegistrationCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class MemberAuthController {

    private final MemberAuthService memberAuthService;


    public MemberAuthController(MemberAuthService memberAuthService) {
        this.memberAuthService = memberAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegistrationCommand command, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        memberAuthService.registerMember(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse login = memberAuthService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return memberAuthService.getOwnInfo(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
