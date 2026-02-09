package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.member.MemberStatsDto;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService - Tests Unitarios")
class MemberServiceImplTest {

    @Mock
    private MemberRepositoryPort memberRepository;

    @Mock
    private FindRepositoryPort findRepository;

    @Mock
    private ResourcePatternResolver resourcePatternResolver;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member1;
    private Member member2;
    private Find validatedFind;
    private Find rejectedFind;
    private Find pendingFind;

    @BeforeEach
    void setUp() {
        member1 = new Member(
                1L, "Juan", "García", "López",
                "juan@test.com", "12345678A", "pass", "600000001",
                UserRole.USER, LocalDateTime.now(), "Galicia", 100
        );

        member2 = new Member(
                2L, "Ana", "Pérez", "Martínez",
                "ana@test.com", "87654321B", "pass", "600000002",
                UserRole.ARCHAEOLOGIST, LocalDateTime.now(), "Madrid", 200
        );

        validatedFind = new Find();
        validatedFind.setId(1L);
        validatedFind.setReporterId(1L);
        validatedFind.setStatus(FindValidationStatus.VALIDATED);
        validatedFind.setDiscoveredAt(LocalDateTime.now());
        validatedFind.setLatitude(43.0);
        validatedFind.setLongitude(-8.0);
        validatedFind.setDescription("Validated find");

        rejectedFind = new Find();
        rejectedFind.setId(2L);
        rejectedFind.setReporterId(1L);
        rejectedFind.setStatus(FindValidationStatus.REJECTED);
        rejectedFind.setDiscoveredAt(LocalDateTime.now());
        rejectedFind.setLatitude(43.0);
        rejectedFind.setLongitude(-8.0);
        rejectedFind.setDescription("Rejected find");

        pendingFind = new Find();
        pendingFind.setId(3L);
        pendingFind.setReporterId(1L);
        pendingFind.setStatus(FindValidationStatus.PENDING);
        pendingFind.setDiscoveredAt(LocalDateTime.now());
        pendingFind.setLatitude(43.0);
        pendingFind.setLongitude(-8.0);
        pendingFind.setDescription("Pending find");
    }

    // ========== TESTS DE getAllMembers ==========

    @Test
    @DisplayName("Debería obtener todos los miembros correctamente")
    void shouldGetAllMembersSuccessfully() {
        // Given
        when(memberRepository.findAll()).thenReturn(Arrays.asList(member1, member2));

        // When
        List<MemberDto> result = memberService.getAllMembers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("juan@test.com");
        assertThat(result.get(0).getName()).isEqualTo("Juan");
        assertThat(result.get(1).getEmail()).isEqualTo("ana@test.com");
        assertThat(result.get(1).getName()).isEqualTo("Ana");

        verify(memberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando no hay miembros")
    void shouldReturnEmptyListWhenNoMembers() {
        // Given
        when(memberRepository.findAll()).thenReturn(List.of());

        // When
        List<MemberDto> result = memberService.getAllMembers();

        // Then
        assertThat(result).isEmpty();
        verify(memberRepository, times(1)).findAll();
    }

    // ========== TESTS DE getMembersWithStats ==========

    @Test
    @DisplayName("Debería obtener miembros con estadísticas correctamente")
    void shouldGetMembersWithStatsSuccessfully() {
        // Given
        when(memberRepository.findAll()).thenReturn(Arrays.asList(member1, member2));
        when(findRepository.findByReporterId(1L))
                .thenReturn(Arrays.asList(validatedFind, rejectedFind, pendingFind));
        when(findRepository.findByReporterId(2L)).thenReturn(List.of());

        // When
        List<MemberStatsDto> result = memberService.getMembersWithStats();

        // Then
        assertThat(result).hasSize(2);

        MemberStatsDto stats1 = result.get(0);
        assertThat(stats1.userId()).isEqualTo(1L);
        assertThat(stats1.userName()).isEqualTo("Juan García");
        assertThat(stats1.email()).isEqualTo("juan@test.com");
        assertThat(stats1.role()).isEqualTo("USER");
        assertThat(stats1.totalFinds()).isEqualTo(3);
        assertThat(stats1.validatedFinds()).isEqualTo(1);
        assertThat(stats1.rejectedFinds()).isEqualTo(1);
        assertThat(stats1.pendingFinds()).isEqualTo(1);

        MemberStatsDto stats2 = result.get(1);
        assertThat(stats2.userId()).isEqualTo(2L);
        assertThat(stats2.userName()).isEqualTo("Ana Pérez");
        assertThat(stats2.totalFinds()).isEqualTo(0);
        assertThat(stats2.validatedFinds()).isEqualTo(0);
        assertThat(stats2.rejectedFinds()).isEqualTo(0);
        assertThat(stats2.pendingFinds()).isEqualTo(0);

        verify(memberRepository, times(1)).findAll();
        verify(findRepository, times(1)).findByReporterId(1L);
        verify(findRepository, times(1)).findByReporterId(2L);
    }

    @Test
    @DisplayName("Debería calcular correctamente estadísticas con solo hallazgos validados")
    void shouldCalculateStatsWithOnlyValidatedFinds() {
        // Given
        Find validatedFind2 = new Find();
        validatedFind2.setId(4L);
        validatedFind2.setReporterId(1L);
        validatedFind2.setStatus(FindValidationStatus.VALIDATED);
        validatedFind2.setDiscoveredAt(LocalDateTime.now());
        validatedFind2.setLatitude(43.0);
        validatedFind2.setLongitude(-8.0);
        validatedFind2.setDescription("Another validated");

        when(memberRepository.findAll()).thenReturn(List.of(member1));
        when(findRepository.findByReporterId(1L))
                .thenReturn(Arrays.asList(validatedFind, validatedFind2));

        // When
        List<MemberStatsDto> result = memberService.getMembersWithStats();

        // Then
        assertThat(result).hasSize(1);
        MemberStatsDto stats = result.get(0);
        assertThat(stats.totalFinds()).isEqualTo(2);
        assertThat(stats.validatedFinds()).isEqualTo(2);
        assertThat(stats.rejectedFinds()).isEqualTo(0);
        assertThat(stats.pendingFinds()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debería calcular correctamente estadísticas con solo hallazgos rechazados")
    void shouldCalculateStatsWithOnlyRejectedFinds() {
        // Given
        Find rejectedFind2 = new Find();
        rejectedFind2.setId(5L);
        rejectedFind2.setReporterId(1L);
        rejectedFind2.setStatus(FindValidationStatus.REJECTED);
        rejectedFind2.setDiscoveredAt(LocalDateTime.now());
        rejectedFind2.setLatitude(43.0);
        rejectedFind2.setLongitude(-8.0);
        rejectedFind2.setDescription("Another rejected");

        when(memberRepository.findAll()).thenReturn(List.of(member1));
        when(findRepository.findByReporterId(1L))
                .thenReturn(Arrays.asList(rejectedFind, rejectedFind2));

        // When
        List<MemberStatsDto> result = memberService.getMembersWithStats();

        // Then
        assertThat(result).hasSize(1);
        MemberStatsDto stats = result.get(0);
        assertThat(stats.totalFinds()).isEqualTo(2);
        assertThat(stats.validatedFinds()).isEqualTo(0);
        assertThat(stats.rejectedFinds()).isEqualTo(2);
        assertThat(stats.pendingFinds()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debería retornar lista vacía cuando no hay miembros para stats")
    void shouldReturnEmptyListWhenNoMembersForStats() {
        // Given
        when(memberRepository.findAll()).thenReturn(List.of());

        // When
        List<MemberStatsDto> result = memberService.getMembersWithStats();

        // Then
        assertThat(result).isEmpty();
        verify(findRepository, never()).findByReporterId(anyLong());
    }

    // ========== TESTS DE findByEmail ==========

    @Test
    @DisplayName("Debería encontrar un miembro por email")
    void shouldFindMemberByEmail() {
        // Given
        when(memberRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(member1));

        // When
        Optional<MemberDto> result = memberService.findByEmail("juan@test.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("juan@test.com");
        assertThat(result.get().getName()).isEqualTo("Juan");
        assertThat(result.get().getDni()).isEqualTo("12345678A");

        verify(memberRepository, times(1)).findByEmail("juan@test.com");
    }

    @Test
    @DisplayName("Debería retornar Optional.empty cuando el email no existe")
    void shouldReturnEmptyWhenEmailNotFound() {
        // Given
        when(memberRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When
        Optional<MemberDto> result = memberService.findByEmail("nonexistent@test.com");

        // Then
        assertThat(result).isEmpty();
        verify(memberRepository, times(1)).findByEmail("nonexistent@test.com");
    }

    // ========== TESTS DE getTopByReputation ==========

    @Test
    @DisplayName("Debería obtener top miembros por reputación")
    void shouldGetTopMembersByReputation() {
        // Given
        Member member3 = new Member(
                3L, "Carlos", "Ruiz", "Sánchez",
                "carlos@test.com", "11111111C", "pass", "600000003",
                UserRole.USER, LocalDateTime.now(), "Barcelona", 300
        );

        when(memberRepository.findAllByReputation(3))
                .thenReturn(Arrays.asList(member3, member2, member1)); // Ordenados por reputación desc

        // When
        List<Member> result = memberService.getTopByReputation(3);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getReputation()).isEqualTo(300);
        assertThat(result.get(1).getReputation()).isEqualTo(200);
        assertThat(result.get(2).getReputation()).isEqualTo(100);

        verify(memberRepository, times(1)).findAllByReputation(3);
    }

    @Test
    @DisplayName("Debería obtener top 10 miembros por reputación")
    void shouldGetTop10MembersByReputation() {
        // Given
        when(memberRepository.findAllByReputation(10)).thenReturn(Arrays.asList(member2, member1));

        // When
        List<Member> result = memberService.getTopByReputation(10);

        // Then
        assertThat(result).hasSize(2);
        verify(memberRepository, times(1)).findAllByReputation(10);
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay miembros con reputación")
    void shouldReturnEmptyListWhenNoMembersWithReputation() {
        // Given
        when(memberRepository.findAllByReputation(10)).thenReturn(List.of());

        // When
        List<Member> result = memberService.getTopByReputation(10);

        // Then
        assertThat(result).isEmpty();
        verify(memberRepository, times(1)).findAllByReputation(10);
    }

    // ========== TESTS DE incrementReputation ==========

    @Test
    @DisplayName("Debería incrementar la reputación correctamente")
    void shouldIncrementReputationSuccessfully() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        // When
        memberService.incrementReputation(1L, 10);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getReputation()).isEqualTo(110); // 100 + 10

        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("Debería incrementar reputación con valor negativo (decrementar)")
    void shouldDecrementReputationWithNegativeValue() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        // When
        memberService.incrementReputation(1L, -20);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getReputation()).isEqualTo(80); // 100 - 20
    }

    @Test
    @DisplayName("Debería incrementar reputación con 1 punto")
    void shouldIncrementReputationByOne() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        // When
        memberService.incrementReputation(1L, 1);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getReputation()).isEqualTo(101);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el miembro no existe al incrementar reputación")
    void shouldThrowExceptionWhenMemberNotFoundForReputationIncrement() {
        // Given
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.incrementReputation(999L, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Member not found");

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería mantener reputación si se incrementa con 0")
    void shouldMaintainReputationWhenIncrementedByZero() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        // When
        memberService.incrementReputation(1L, 0);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());

        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getReputation()).isEqualTo(100); // Sin cambios
    }
}