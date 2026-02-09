package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.auth.LoginRequest;
import es.marcos.digreport.application.dto.auth.LoginResponse;
import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.port.in.command.UserRegistrationCommand;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.exception.DuplicatedDniException;
import es.marcos.digreport.domain.exception.DuplicatedEmailException;
import es.marcos.digreport.domain.exception.ValidationException;
import es.marcos.digreport.domain.model.Member;
import es.marcos.digreport.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberAuthService - Tests Unitarios")
class MemberAuthServiceImplTest {

    @Mock
    private MemberRepositoryPort memberRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private MemberAuthServiceImpl memberAuthService;

    private UserRegistrationCommand validCommand;
    private Member validMember;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        validCommand = new UserRegistrationCommand();
        validCommand.setName("Juan");
        validCommand.setSurname1("García");
        validCommand.setSurname2("López");
        validCommand.setEmail("juan@test.com");
        validCommand.setConfirmEmail("juan@test.com");
        validCommand.setDni("12345678A");
        validCommand.setMobile("600000001");
        validCommand.setPassword("password123");
        validCommand.setConfirmPassword("password123");
        validCommand.setCcaa("Galicia");

        validMember = new Member(
                1L, "Juan", "García", "López",
                "juan@test.com", "12345678A", "hashedPassword", "600000001",
                UserRole.USER, LocalDateTime.now(), "Galicia", 0
        );

        validLoginRequest = new LoginRequest();
        // Necesitamos usar reflection para setear los campos private
        setPrivateField(validLoginRequest, "email", "juan@test.com");
        setPrivateField(validLoginRequest, "password", "password123");
    }

    // ========== TESTS DE registerMember ==========

    @Test
    @DisplayName("Debería registrar un miembro correctamente")
    void shouldRegisterMemberSuccessfully() {
        // Given
        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni("12345678A")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(validMember);

        // When
        Member result = memberAuthService.registerMember(validCommand);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("juan@test.com");
        assertThat(result.getDni()).isEqualTo("12345678A");
        assertThat(result.getRole()).isEqualTo(UserRole.USER);
        assertThat(result.getReputation()).isEqualTo(0);

        verify(memberRepositoryPort).findByEmail("juan@test.com");
        verify(memberRepositoryPort).findByDni("12345678A");
        verify(passwordEncoder).encode("password123");
        verify(memberRepositoryPort).save(any(Member.class));
    }

    @Test
    @DisplayName("Debería normalizar email a minúsculas")
    void shouldNormalizeEmailToLowerCase() {
        // Given
        validCommand.setEmail("JUAN@TEST.COM");
        validCommand.setConfirmEmail("JUAN@TEST.COM");

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(validMember);

        // When
        memberAuthService.registerMember(validCommand);

        // Then
        verify(memberRepositoryPort).findByEmail("juan@test.com");
    }

    @Test
    @DisplayName("Debería normalizar DNI a mayúsculas")
    void shouldNormalizeDniToUpperCase() {
        // Given
        validCommand.setDni("12345678a");

        when(memberRepositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni("12345678A")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(validMember);

        // When
        memberAuthService.registerMember(validCommand);

        // Then
        verify(memberRepositoryPort).findByDni("12345678A");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre está vacío")
    void shouldThrowExceptionWhenNameIsBlank() {
        // Given
        validCommand.setName("");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'name' is required");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el email está vacío")
    void shouldThrowExceptionWhenEmailIsBlank() {
        // Given
        validCommand.setEmail("");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'email' is required");
    }

    @Test
    @DisplayName("Debería lanzar excepción si los emails no coinciden")
    void shouldThrowExceptionWhenEmailsDoNotMatch() {
        // Given
        validCommand.setEmail("juan@test.com");
        validCommand.setConfirmEmail("otro@test.com");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Emails do not match");
    }

    @Test
    @DisplayName("Debería lanzar excepción si las contraseñas no coinciden")
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        // Given
        validCommand.setPassword("password123");
        validCommand.setConfirmPassword("different456");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Passwords do not match");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el formato del email es inválido")
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // Given
        validCommand.setEmail("invalid-email");
        validCommand.setConfirmEmail("invalid-email");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el formato del DNI es inválido")
    void shouldThrowExceptionWhenDniFormatIsInvalid() {
        // Given
        validCommand.setDni("123456"); // DNI sin letra

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid DNI format (expected 8 digits + letter)");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(validMember));

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(DuplicatedEmailException.class)
                .hasMessage("Email already exists: juan@test.com");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el DNI ya existe")
    void shouldThrowExceptionWhenDniAlreadyExists() {
        // Given
        when(memberRepositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni("12345678A")).thenReturn(Optional.of(validMember));

        // When & Then
        assertThatThrownBy(() -> memberAuthService.registerMember(validCommand))
                .isInstanceOf(DuplicatedDniException.class)
                .hasMessage("DNI already exists: 12345678A");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería hashear la contraseña antes de guardar")
    void shouldHashPasswordBeforeSaving() {
        // Given
        when(memberRepositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepositoryPort.save(memberCaptor.capture())).thenReturn(validMember);

        // When
        memberAuthService.registerMember(validCommand);

        // Then
        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getPassword()).isEqualTo("hashedPassword");
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Debería asignar rol USER por defecto")
    void shouldAssignUserRoleByDefault() {
        // Given
        when(memberRepositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepositoryPort.save(memberCaptor.capture())).thenReturn(validMember);

        // When
        memberAuthService.registerMember(validCommand);

        // Then
        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Debería asignar reputación inicial de 0")
    void shouldAssignInitialReputationOfZero() {
        // Given
        when(memberRepositoryPort.findByEmail(anyString())).thenReturn(Optional.empty());
        when(memberRepositoryPort.findByDni(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        when(memberRepositoryPort.save(memberCaptor.capture())).thenReturn(validMember);

        // When
        memberAuthService.registerMember(validCommand);

        // Then
        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getReputation()).isEqualTo(0);
    }

    // ========== TESTS DE login ==========

    @Test
    @DisplayName("Debería realizar login correctamente")
    void shouldLoginSuccessfully() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Authentication exitosa
        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(validMember));
        when(jwtService.generateToken(validMember)).thenReturn("mock-jwt-token");

        // When
        LoginResponse result = memberAuthService.login(validLoginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mock-jwt-token");
        assertThat(result.getUsername()).isEqualTo("Juan");
        assertThat(result.getRole()).isEqualTo(UserRole.USER);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(validMember);
    }

    @Test
    @DisplayName("Debería normalizar email a minúsculas en login")
    void shouldNormalizeEmailToLowerCaseInLogin() {
        // Given
        LoginRequest upperCaseLoginRequest = new LoginRequest();
        setPrivateField(upperCaseLoginRequest, "email", "JUAN@TEST.COM");
        setPrivateField(upperCaseLoginRequest, "password", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(validMember));
        when(jwtService.generateToken(any())).thenReturn("token");

        // When
        memberAuthService.login(upperCaseLoginRequest);

        // Then
        verify(memberRepositoryPort).findByEmail("juan@test.com");
    }

    @Test
    @DisplayName("Debería lanzar excepción si LoginRequest es null")
    void shouldThrowExceptionWhenLoginRequestIsNull() {
        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(null))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");
    }

    @Test
    @DisplayName("Debería lanzar excepción si email es null en login")
    void shouldThrowExceptionWhenEmailIsNullInLogin() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        setPrivateField(loginRequest, "email", null);
        setPrivateField(loginRequest, "password", "password123");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");
    }

    @Test
    @DisplayName("Debería lanzar excepción si password es null en login")
    void shouldThrowExceptionWhenPasswordIsNullInLogin() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        setPrivateField(loginRequest, "email", "juan@test.com");
        setPrivateField(loginRequest, "password", null);

        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");
    }

    @Test
    @DisplayName("Debería lanzar excepción si email está vacío en login")
    void shouldThrowExceptionWhenEmailIsEmptyInLogin() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        setPrivateField(loginRequest, "email", "");
        setPrivateField(loginRequest, "password", "password123");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");
    }

    @Test
    @DisplayName("Debería lanzar excepción si password está vacío en login")
    void shouldThrowExceptionWhenPasswordIsBlankInLogin() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        setPrivateField(loginRequest, "email", "juan@test.com");
        setPrivateField(loginRequest, "password", "");

        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");
    }

    @Test
    @DisplayName("Debería lanzar excepción si las credenciales son incorrectas")
    void shouldThrowExceptionWhenCredentialsAreIncorrect() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(validLoginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si falla la autenticación por cualquier motivo")
    void shouldThrowExceptionWhenAuthenticationFails() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.InternalAuthenticationServiceException("Auth error"));

        // When & Then
        assertThatThrownBy(() -> memberAuthService.login(validLoginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email or password incorrect");

        verify(jwtService, never()).generateToken(any());
    }

    // ========== TESTS DE getOwnInfo ==========

    @Test
    @DisplayName("Debería obtener información del usuario por email")
    void shouldGetOwnInfo() {
        // Given
        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(validMember));

        // When
        Optional<MemberDto> result = memberAuthService.getOwnInfo("juan@test.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("juan@test.com");
        assertThat(result.get().getName()).isEqualTo("Juan");
        assertThat(result.get().getDni()).isEqualTo("12345678A");
        assertThat(result.get().getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Debería retornar Optional.empty() si el usuario no existe")
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        when(memberRepositoryPort.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When
        Optional<MemberDto> result = memberAuthService.getOwnInfo("nonexistent@test.com");

        // Then
        assertThat(result).isEmpty();
    }

    // ===== Método auxiliar para setear campos privados =====

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Error setting private field: " + fieldName, e);
        }
    }
}