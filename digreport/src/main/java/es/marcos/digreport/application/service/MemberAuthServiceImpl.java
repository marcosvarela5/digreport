package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.auth.LoginRequest;
import es.marcos.digreport.application.dto.auth.LoginResponse;
import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.port.in.MemberAuthService;
import es.marcos.digreport.application.port.in.command.UserRegistrationCommand;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.exception.DuplicatedDniException;
import es.marcos.digreport.domain.exception.DuplicatedEmailException;
import es.marcos.digreport.domain.exception.ValidationException;
import es.marcos.digreport.domain.model.Member;
import es.marcos.digreport.infrastructure.security.jwt.JwtService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static es.marcos.digreport.domain.util.MemberUtils.*;

@Transactional
@Service
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberRepositoryPort memberRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public MemberAuthServiceImpl(MemberRepositoryPort memberRepositoryPort,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        this.memberRepositoryPort = memberRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Member registerMember(UserRegistrationCommand userRegistrationCommand) {

        requireNonBlank(userRegistrationCommand.getName(), "name");
        requireNonBlank(userRegistrationCommand.getSurname1(), "surname1");
        requireNonBlank(userRegistrationCommand.getEmail(), "email");
        requireNonBlank(userRegistrationCommand.getConfirmEmail(), "confirmEmail");   // <- nuevo
        requireNonBlank(userRegistrationCommand.getDni(), "dni");
        requireNonBlank(userRegistrationCommand.getPassword(), "password");
        requireNonBlank(userRegistrationCommand.getConfirmPassword(), "confirmPassword"); // <- nuevo
        requireNonBlank(userRegistrationCommand.getMobile(), "mobile");
        requireNonBlank(userRegistrationCommand.getCcaa(), "ccaa");

        // normalize fields
        String email = userRegistrationCommand.getEmail().trim().toLowerCase();
        String confirmEmail = userRegistrationCommand.getConfirmEmail().trim().toLowerCase(); // <- normalizado
        String dni = userRegistrationCommand.getDni().trim().toUpperCase();


        if (!email.equals(confirmEmail)) {
            throw new ValidationException("Emails do not match");
        }
        if (!userRegistrationCommand.getPassword().equals(userRegistrationCommand.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (!isValidDni(dni)) {
            throw new ValidationException("Invalid DNI format (expected 8 digits + letter)");
        }

        memberRepositoryPort.findByEmail(email).ifPresent(m -> { throw new DuplicatedEmailException(email); });
        memberRepositoryPort.findByDni(dni).ifPresent(m -> { throw new DuplicatedDniException(dni); });

        String hashedPassword = passwordEncoder.encode(userRegistrationCommand.getPassword());

        Member member = new Member(
                null,
                userRegistrationCommand.getName(),
                userRegistrationCommand.getSurname1(),
                userRegistrationCommand.getSurname2(),
                email,
                dni,
                hashedPassword,
                userRegistrationCommand.getMobile(),
                UserRole.USER,
                LocalDateTime.now(),
                userRegistrationCommand.getCcaa()
        );
        return memberRepositoryPort.save(member);
    }


    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        if (loginRequest == null
                || loginRequest.getEmail() == null
                || loginRequest.getPassword() == null) {
            throw new BadCredentialsException("Email or password incorrect");
        }

        String normalizedEmail = loginRequest.getEmail().trim().toLowerCase();

        if (normalizedEmail.isEmpty() || loginRequest.getPassword().isBlank()) {
            throw new BadCredentialsException("Email or password incorrect");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                normalizedEmail,
                loginRequest.getPassword()
        );
        try {
            authenticationManager.authenticate(authentication);
            System.out.println("Login successful");

            Member member = memberRepositoryPort.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtService.generateToken(member);
            return new LoginResponse(token, member.getName(), member.getRole());

        } catch (AuthenticationException e) {
            System.err.println("Authentication error: " + e.getMessage());
            throw new BadCredentialsException("Email or password incorrect");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MemberDto> getOwnInfo(String email) {
        return memberRepositoryPort.findByEmail(email)
                .map(MemberDto::fromDomain);
    }
}

