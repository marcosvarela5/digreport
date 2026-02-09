package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.authority.*;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorityService - Tests Unitarios")
class AuthorityServiceImplTest {

    @Mock
    private FindRepositoryPort findRepository;

    @Mock
    private MemberRepositoryPort memberRepository;

    @InjectMocks
    private AuthorityServiceImpl authorityService;

    private List<Find> mockFinds;
    private Member mockMember1;
    private Member mockMember2;

    @BeforeEach
    void setUp() {
        mockMember1 = new Member(
                1L, "Juan", "García", "López",
                "juan@test.com", "12345678A", "pass", "600000001",
                UserRole.USER, LocalDateTime.now(), "Galicia", 100
        );

        mockMember2 = new Member(
                2L, "María", "Pérez", "Martínez",
                "maria@test.com", "87654321B", "pass", "600000002",
                UserRole.USER, LocalDateTime.now(), "Cataluña", 50
        );

        mockFinds = Arrays.asList(
                createFind(1L, 1L, FindValidationStatus.VALIDATED, "Galicia", LocalDateTime.of(2024, 1, 15, 10, 0)),
                createFind(2L, 1L, FindValidationStatus.VALIDATED, "Galicia", LocalDateTime.of(2024, 1, 20, 10, 0)),
                createFind(3L, 1L, FindValidationStatus.PENDING, "Galicia", LocalDateTime.of(2024, 2, 10, 10, 0)),
                createFind(4L, 2L, FindValidationStatus.VALIDATED, "Cataluña", LocalDateTime.of(2024, 2, 15, 10, 0)),
                createFind(5L, 2L, FindValidationStatus.REJECTED, "Cataluña", LocalDateTime.of(2024, 3, 5, 10, 0))
        );
    }

    @Test
    @DisplayName("Debería calcular correctamente las estadísticas totales del dashboard")
    void shouldCalculateDashboardStatsCorrectly() {
        // Given
        when(findRepository.findAll()).thenReturn(mockFinds);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember1));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(mockMember2));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.totalFinds()).isEqualTo(5);
        assertThat(stats.pendingFinds()).isEqualTo(1);
        assertThat(stats.validatedFinds()).isEqualTo(3);
        assertThat(stats.rejectedFinds()).isEqualTo(1);

        verify(findRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería agrupar correctamente los hallazgos por estado")
    void shouldGroupFindsByStatusCorrectly() {
        // Given
        when(findRepository.findAll()).thenReturn(mockFinds);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember1));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats.findsByStatus())
                .containsEntry("VALIDATED", 3L)
                .containsEntry("PENDING", 1L)
                .containsEntry("REJECTED", 1L);
    }

    @Test
    @DisplayName("Debería calcular correctamente el top 10 de reporteros")
    void shouldCalculateTopReportersCorrectly() {
        // Given
        when(findRepository.findAll()).thenReturn(mockFinds);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember1));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(mockMember2));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        List<TopReporterDto> topReporters = stats.topReporters();
        assertThat(topReporters).hasSize(2);

        TopReporterDto firstReporter = topReporters.get(0);
        assertThat(firstReporter.userId()).isEqualTo(1L);
        assertThat(firstReporter.userName()).isEqualTo("Juan García");
        assertThat(firstReporter.totalFinds()).isEqualTo(3);
        assertThat(firstReporter.validatedFinds()).isEqualTo(2);

        TopReporterDto secondReporter = topReporters.get(1);
        assertThat(secondReporter.userId()).isEqualTo(2L);
        assertThat(secondReporter.userName()).isEqualTo("María Pérez");
        assertThat(secondReporter.totalFinds()).isEqualTo(2);
        assertThat(secondReporter.validatedFinds()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debería limitar los top reporters a 10")
    void shouldLimitTopReportersToTen() {
        // Given
        List<Find> manyFinds = createFindsForMultipleReporters(15);
        when(findRepository.findAll()).thenReturn(manyFinds);

        for (long i = 1; i <= 15; i++) {
            Member member = new Member(
                    i, "User" + i, "Surname" + i, null,
                    "user" + i + "@test.com", "1234567" + i, "pass", "60000000" + i,
                    UserRole.USER, LocalDateTime.now(), "Galicia", 50
            );
            when(memberRepository.findById(i)).thenReturn(Optional.of(member));
        }

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats.topReporters()).hasSize(10);
    }

    @Test
    @DisplayName("Debería excluir reporteros sin miembro asociado")
    void shouldExcludeReportersWithoutMember() {
        // Given
        when(findRepository.findAll()).thenReturn(mockFinds);
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        when(memberRepository.findById(2L)).thenReturn(Optional.of(mockMember2));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats.topReporters()).hasSize(1);
        assertThat(stats.topReporters().get(0).userId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Debería calcular correctamente hallazgos por CCAA")
    void shouldCalculateFindsByCcaaCorrectly() {
        // Given
        when(findRepository.findAll()).thenReturn(mockFinds);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember1));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        List<FindsByRegionDto> byCcaa = stats.findsByCcaa();
        assertThat(byCcaa).hasSize(2);

        assertThat(byCcaa.get(0).ccaa()).isEqualTo("Galicia");
        assertThat(byCcaa.get(0).count()).isEqualTo(3);
        assertThat(byCcaa.get(1).ccaa()).isEqualTo("Cataluña");
        assertThat(byCcaa.get(1).count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Debería filtrar hallazgos sin CCAA en estadísticas por región")
    void shouldFilterFindsWithoutCcaaInRegionStats() {
        // Given
        List<Find> findsWithNullCcaa = Arrays.asList(
                createFind(1L, 1L, FindValidationStatus.VALIDATED, null, LocalDateTime.now()),
                createFind(2L, 1L, FindValidationStatus.VALIDATED, "", LocalDateTime.now()),
                createFind(3L, 1L, FindValidationStatus.VALIDATED, "Galicia", LocalDateTime.now())
        );
        when(findRepository.findAll()).thenReturn(findsWithNullCcaa);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember1));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats.findsByCcaa()).hasSize(1);
        assertThat(stats.findsByCcaa().get(0).ccaa()).isEqualTo("Galicia");
    }

    @Test
    @DisplayName("Debería calcular estadísticas mensuales correctamente")
    void shouldCalculateMonthlyStatsCorrectly() {
        // Given
        when(findRepository.findAll()).thenReturn(mockFinds);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mockMember1));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        List<MonthlyStatsDto> monthlyStats = stats.monthlyStats();
        assertThat(monthlyStats).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Debería retornar estadísticas vacías cuando no hay hallazgos")
    void shouldReturnEmptyStatsWhenNoFinds() {
        // Given
        when(findRepository.findAll()).thenReturn(List.of());

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats.totalFinds()).isZero();
        assertThat(stats.pendingFinds()).isZero();
        assertThat(stats.validatedFinds()).isZero();
        assertThat(stats.rejectedFinds()).isZero();
        assertThat(stats.topReporters()).isEmpty();
        assertThat(stats.findsByCcaa()).isEmpty();
        assertThat(stats.monthlyStats()).isEmpty();

        verify(memberRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Debería manejar hallazgos con estado REVOKED")
    void shouldHandleRevokedStatus() {
        // Given
        List<Find> findsWithRevoked = Arrays.asList(
                createFind(1L, 1L, FindValidationStatus.VALIDATED, "Galicia", LocalDateTime.now()),
                createFind(2L, 1L, FindValidationStatus.REVOKED, "Galicia", LocalDateTime.now())
        );
        when(findRepository.findAll()).thenReturn(findsWithRevoked);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember1));

        // When
        DashboardStatsDto stats = authorityService.getDashboardStats();

        // Then
        assertThat(stats.totalFinds()).isEqualTo(2);
        assertThat(stats.findsByStatus()).containsEntry("REVOKED", 1L);
    }

    // ===== Métodos auxiliares =====

    private Find createFind(Long id, Long reporterId, FindValidationStatus status,
                            String ccaa, LocalDateTime discoveredAt) {
        Find find = new Find();
        find.setId(id);
        find.setReporterId(reporterId);
        find.setStatus(status);
        find.setCcaa(ccaa);
        find.setDiscoveredAt(discoveredAt);
        find.setLatitude(43.0);
        find.setLongitude(-8.0);
        find.setDescription("Test description");
        return find;
    }

    private List<Find> createFindsForMultipleReporters(int numberOfReporters) {
        List<Find> finds = new java.util.ArrayList<>();
        for (long i = 1; i <= numberOfReporters; i++) {
            finds.add(createFind(i, i, FindValidationStatus.VALIDATED,
                    "Region" + i, LocalDateTime.now()));
        }
        return finds;
    }
}