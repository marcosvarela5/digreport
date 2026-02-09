package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.stats.PublicStatsDto;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatsService - Tests Unitarios")
class StatsServiceImplTest {

    @Mock
    private MemberRepositoryPort memberRepositoryPort;

    @Mock
    private FindRepositoryPort findRepositoryPort;

    @InjectMocks
    private StatsServiceImpl statsService;

    // ========== TESTS DE getPublicStats ==========

    @Test
    @DisplayName("Debería calcular estadísticas públicas correctamente")
    void shouldCalculatePublicStatsCorrectly() {
        // Given
        when(findRepositoryPort.count()).thenReturn(100L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(20L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(70L);
        when(memberRepositoryPort.count()).thenReturn(50L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(10L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.totalFinds()).isEqualTo(100L);
        assertThat(result.pendingFinds()).isEqualTo(20L);
        assertThat(result.totalArchaeologists()).isEqualTo(10L);
        assertThat(result.totalCitizens()).isEqualTo(50L);
        assertThat(result.validationRate()).isEqualTo(70.0);

        verify(findRepositoryPort, times(1)).count();
        verify(findRepositoryPort, times(1)).countByStatus(FindValidationStatus.PENDING);
        verify(findRepositoryPort, times(1)).countByStatus(FindValidationStatus.VALIDATED);
        verify(memberRepositoryPort, times(1)).count();
        verify(memberRepositoryPort, times(1)).countByRole(UserRole.ARCHAEOLOGIST);
    }

    @Test
    @DisplayName("Debería calcular tasa de validación del 100% cuando todos están validados")
    void shouldCalculate100PercentValidationRateWhenAllValidated() {
        // Given
        when(findRepositoryPort.count()).thenReturn(50L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(50L);
        when(memberRepositoryPort.count()).thenReturn(30L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(5L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.validationRate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Debería calcular tasa de validación del 0% cuando ninguno está validado")
    void shouldCalculate0PercentValidationRateWhenNoneValidated() {
        // Given
        when(findRepositoryPort.count()).thenReturn(50L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(50L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(0L);
        when(memberRepositoryPort.count()).thenReturn(30L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(5L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.validationRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debería calcular tasa de validación del 50% correctamente")
    void shouldCalculate50PercentValidationRate() {
        // Given
        when(findRepositoryPort.count()).thenReturn(100L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(30L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(50L);
        when(memberRepositoryPort.count()).thenReturn(40L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(8L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.validationRate()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Debería calcular tasa de validación con decimales correctamente")
    void shouldCalculateValidationRateWithDecimals() {
        // Given
        when(findRepositoryPort.count()).thenReturn(3L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(1L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(2L);
        when(memberRepositoryPort.count()).thenReturn(10L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(2L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.validationRate()).isCloseTo(66.666666, within(0.0001));
    }

    @Test
    @DisplayName("Debería retornar 100% de validación cuando no hay hallazgos")
    void shouldReturn100PercentValidationRateWhenNoFinds() {
        // Given
        when(findRepositoryPort.count()).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(0L);
        when(memberRepositoryPort.count()).thenReturn(20L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(3L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.totalFinds()).isEqualTo(0L);
        assertThat(result.validationRate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Debería retornar 0 hallazgos pendientes cuando todos están procesados")
    void shouldReturn0PendingFindsWhenAllProcessed() {
        // Given
        when(findRepositoryPort.count()).thenReturn(100L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(100L);
        when(memberRepositoryPort.count()).thenReturn(50L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(10L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.pendingFinds()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Debería manejar sistema sin arqueólogos")
    void shouldHandleSystemWithoutArchaeologists() {
        // Given
        when(findRepositoryPort.count()).thenReturn(50L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(50L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(0L);
        when(memberRepositoryPort.count()).thenReturn(20L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(0L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.totalArchaeologists()).isEqualTo(0L);
        assertThat(result.totalCitizens()).isEqualTo(20L);
    }

    @Test
    @DisplayName("Debería manejar sistema sin usuarios")
    void shouldHandleSystemWithoutUsers() {
        // Given
        when(findRepositoryPort.count()).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(0L);
        when(memberRepositoryPort.count()).thenReturn(0L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(0L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.totalFinds()).isEqualTo(0L);
        assertThat(result.totalCitizens()).isEqualTo(0L);
        assertThat(result.totalArchaeologists()).isEqualTo(0L);
        assertThat(result.pendingFinds()).isEqualTo(0L);
        assertThat(result.validationRate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Debería manejar un solo hallazgo validado")
    void shouldHandleSingleValidatedFind() {
        // Given
        when(findRepositoryPort.count()).thenReturn(1L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(0L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(1L);
        when(memberRepositoryPort.count()).thenReturn(5L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(1L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.totalFinds()).isEqualTo(1L);
        assertThat(result.pendingFinds()).isEqualTo(0L);
        assertThat(result.validationRate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Debería manejar un solo hallazgo pendiente")
    void shouldHandleSinglePendingFind() {
        // Given
        when(findRepositoryPort.count()).thenReturn(1L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(1L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(0L);
        when(memberRepositoryPort.count()).thenReturn(5L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(1L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.totalFinds()).isEqualTo(1L);
        assertThat(result.pendingFinds()).isEqualTo(1L);
        assertThat(result.validationRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Debería manejar grandes volúmenes de datos")
    void shouldHandleLargeVolumes() {
        // Given
        when(findRepositoryPort.count()).thenReturn(1_000_000L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.PENDING)).thenReturn(250_000L);
        when(findRepositoryPort.countByStatus(FindValidationStatus.VALIDATED)).thenReturn(750_000L);
        when(memberRepositoryPort.count()).thenReturn(50_000L);
        when(memberRepositoryPort.countByRole(UserRole.ARCHAEOLOGIST)).thenReturn(5_000L);

        // When
        PublicStatsDto result = statsService.getPublicStats();

        // Then
        assertThat(result.totalFinds()).isEqualTo(1_000_000L);
        assertThat(result.pendingFinds()).isEqualTo(250_000L);
        assertThat(result.totalCitizens()).isEqualTo(50_000L);
        assertThat(result.totalArchaeologists()).isEqualTo(5_000L);
        assertThat(result.validationRate()).isEqualTo(75.0);
    }
}