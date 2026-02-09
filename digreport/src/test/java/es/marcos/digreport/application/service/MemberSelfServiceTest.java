package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.profile.UpdateProfileRequest;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.exception.DuplicatedDniException;
import es.marcos.digreport.domain.exception.DuplicatedEmailException;
import es.marcos.digreport.domain.exception.ValidationException;
import es.marcos.digreport.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberSelfService - Tests Unitarios")
class MemberSelfServiceImplTest {

    @Mock
    private MemberRepositoryPort memberRepositoryPort;

    @InjectMocks
    private MemberSelfServiceImpl memberSelfService;

    private Member existingMember;

    @BeforeEach
    void setUp() {
        existingMember = new Member(
                1L, "Juan", "García", "López",
                "juan@test.com", "12345678A", "hashedPassword", "600000001",
                UserRole.USER, LocalDateTime.now(), "Galicia", 100
        );
    }

    // ========== TESTS DE updateProfile - Actualización exitosa ==========

    @Test
    @DisplayName("Debería actualizar el nombre correctamente")
    void shouldUpdateNameSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Carlos", null, null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        MemberDto result = memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo("Carlos");
        assertThat(savedMember.getSurname1()).isEqualTo("García"); // No cambió
    }

    @Test
    @DisplayName("Debería actualizar surname1 correctamente")
    void shouldUpdateSurname1Successfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, "Pérez", null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        assertThat(memberCaptor.getValue().getSurname1()).isEqualTo("Pérez");
    }

    @Test
    @DisplayName("Debería actualizar surname2 correctamente")
    void shouldUpdateSurname2Successfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, "Martínez", null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        assertThat(memberCaptor.getValue().getSurname2()).isEqualTo("Martínez");
    }

    @Test
    @DisplayName("Debería actualizar el email correctamente")
    void shouldUpdateEmailSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, "nuevo@test.com", null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        assertThat(memberCaptor.getValue().getEmail()).isEqualTo("nuevo@test.com");
        verify(memberRepositoryPort).existsByEmail("nuevo@test.com");
    }

    @Test
    @DisplayName("Debería actualizar el DNI correctamente")
    void shouldUpdateDniSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, "87654321B", null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.existsByDni("87654321B")).thenReturn(false);
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        assertThat(memberCaptor.getValue().getDni()).isEqualTo("87654321B");
        verify(memberRepositoryPort).existsByDni("87654321B");
    }

    @Test
    @DisplayName("Debería actualizar el móvil correctamente")
    void shouldUpdateMobileSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, null, "699999999", null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        assertThat(memberCaptor.getValue().getMobile()).isEqualTo("699999999");
    }

    @Test
    @DisplayName("Debería actualizar la CCAA correctamente")
    void shouldUpdateCcaaSuccessfully() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, null, null, "Andalucía"
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        assertThat(memberCaptor.getValue().getCcaa()).isEqualTo("Andalucía");
    }

    @Test
    @DisplayName("Debería actualizar múltiples campos a la vez")
    void shouldUpdateMultipleFieldsAtOnce() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Carlos", "Pérez", "Martínez", null, null, "699999999", "Madrid"
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo("Carlos");
        assertThat(savedMember.getSurname1()).isEqualTo("Pérez");
        assertThat(savedMember.getSurname2()).isEqualTo("Martínez");
        assertThat(savedMember.getMobile()).isEqualTo("699999999");
        assertThat(savedMember.getCcaa()).isEqualTo("Madrid");
    }

    // ========== TESTS DE normalización ==========

    @Test
    @DisplayName("Debería normalizar el email actual a minúsculas")
    void shouldNormalizeCurrentEmailToLowerCase() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Carlos", null, null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("JUAN@TEST.COM", request);

        // Then
        verify(memberRepositoryPort).findByEmail("juan@test.com");
    }

    @Test
    @DisplayName("Debería normalizar el nuevo email a minúsculas")
    void shouldNormalizeNewEmailToLowerCase() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, "NUEVO@TEST.COM", null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.existsByEmail("nuevo@test.com")).thenReturn(false);
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        verify(memberRepositoryPort).existsByEmail("nuevo@test.com");
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getEmail()).isEqualTo("nuevo@test.com");
    }

    @Test
    @DisplayName("Debería normalizar el DNI a mayúsculas")
    void shouldNormalizeDniToUpperCase() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, "87654321b", null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.existsByDni("87654321B")).thenReturn(false);
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        verify(memberRepositoryPort).existsByDni("87654321B");
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getDni()).isEqualTo("87654321B");
    }

    @Test
    @DisplayName("Debería hacer trim de los campos de texto")
    void shouldTrimTextFields() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "  Carlos  ", "  Pérez  ", "  Martínez  ", null, null, "  699999999  ", "  Madrid  "
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo("Carlos");
        assertThat(savedMember.getSurname1()).isEqualTo("Pérez");
        assertThat(savedMember.getSurname2()).isEqualTo("Martínez");
        assertThat(savedMember.getMobile()).isEqualTo("699999999");
        assertThat(savedMember.getCcaa()).isEqualTo("Madrid");
    }

    // ========== TESTS DE validación ==========

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Carlos", null, null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("nonexistent@test.com", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with email: nonexistent@test.com");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre está vacío")
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "   ", null, null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("name can't be empty");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si surname1 está vacío")
    void shouldThrowExceptionWhenSurname1IsEmpty() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, "   ", null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("surname1 can't be empty");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el móvil está vacío")
    void shouldThrowExceptionWhenMobileIsEmpty() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, null, "   ", null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("mobile can't be empty");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si la CCAA está vacía")
    void shouldThrowExceptionWhenCcaaIsEmpty() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, null, null, "   "
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("ccaa can't be empty");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el formato del email es inválido")
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, "email-invalido", null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Format not valid: Email");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el formato del DNI es inválido")
    void shouldThrowExceptionWhenDniFormatIsInvalid() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, "123456", null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Format not valid: DNI");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nuevo email ya existe")
    void shouldThrowExceptionWhenNewEmailAlreadyExists() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, "otro@test.com", null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.existsByEmail("otro@test.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(DuplicatedEmailException.class)
                .hasMessage("Email already exists: otro@test.com");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nuevo DNI ya existe")
    void shouldThrowExceptionWhenNewDniAlreadyExists() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, "87654321B", null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.existsByDni("87654321B")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> memberSelfService.updateProfile("juan@test.com", request))
                .isInstanceOf(DuplicatedDniException.class)
                .hasMessage("DNI already exists: 87654321B");

        verify(memberRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("No debería verificar duplicación si el email no cambió")
    void shouldNotCheckDuplicationWhenEmailDoesNotChange() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, "juan@test.com", null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        verify(memberRepositoryPort, never()).existsByEmail(anyString());
        verify(memberRepositoryPort).save(any(Member.class));
    }

    @Test
    @DisplayName("No debería verificar duplicación si el DNI no cambió")
    void shouldNotCheckDuplicationWhenDniDoesNotChange() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, "12345678A", null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        verify(memberRepositoryPort, never()).existsByDni(anyString());
        verify(memberRepositoryPort).save(any(Member.class));
    }

    @Test
    @DisplayName("No debería actualizar nada si todos los campos son null")
    void shouldNotUpdateWhenAllFieldsAreNull() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                null, null, null, null, null, null, null
        );

        when(memberRepositoryPort.findByEmail("juan@test.com")).thenReturn(Optional.of(existingMember));
        when(memberRepositoryPort.save(any(Member.class))).thenReturn(existingMember);

        // When
        memberSelfService.updateProfile("juan@test.com", request);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepositoryPort).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo("Juan");
        assertThat(savedMember.getSurname1()).isEqualTo("García");
        assertThat(savedMember.getEmail()).isEqualTo("juan@test.com");
        assertThat(savedMember.getDni()).isEqualTo("12345678A");
    }
}