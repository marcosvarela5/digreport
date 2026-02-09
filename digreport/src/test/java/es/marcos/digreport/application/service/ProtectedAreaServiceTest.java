package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.protectedarea.CreateProtectedAreaDto;
import es.marcos.digreport.application.dto.protectedarea.ProtectedAreaDto;
import es.marcos.digreport.application.port.out.ProtectedAreaRepositoryPort;
import es.marcos.digreport.domain.enums.ProtectedAreaType;
import es.marcos.digreport.domain.enums.ProtectionType;
import es.marcos.digreport.domain.model.ProtectedArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProtectedAreaService - Tests Unitarios")
class ProtectedAreaServiceImplTest {

    @Mock
    private ProtectedAreaRepositoryPort repository;

    @InjectMocks
    private ProtectedAreaServiceImpl protectedAreaService;

    private CreateProtectedAreaDto createDto;
    private ProtectedArea existingArea;

    @BeforeEach
    void setUp() {
        createDto = new CreateProtectedAreaDto(
                "Parque Nacional de Galicia",
                "Descripción del parque",
                ProtectedAreaType.AREA,
                ProtectionType.PARQUE_NACIONAL,
                "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}",
                "Galicia"
        );

        existingArea = ProtectedArea.builder()
                .id(1L)
                .name("Parque Nacional de Galicia")
                .description("Descripción del parque")
                .type(ProtectedAreaType.AREA)
                .protectionType(ProtectionType.PARQUE_NACIONAL)
                .geometry("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}")
                .ccaa("Galicia")
                .color("#FFFF00")
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    // ========== TESTS DE createProtectedArea ==========

    @Test
    @DisplayName("Debería crear un área protegida correctamente")
    void shouldCreateProtectedAreaSuccessfully() {
        // Given
        when(repository.save(any(ProtectedArea.class))).thenReturn(existingArea);

        // When
        ProtectedAreaDto result = protectedAreaService.createProtectedArea(createDto, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Parque Nacional de Galicia");
        assertThat(result.type()).isEqualTo(ProtectedAreaType.AREA);
        assertThat(result.protectionType()).isEqualTo(ProtectionType.PARQUE_NACIONAL);
        assertThat(result.color()).isEqualTo("#FFFF00");
        assertThat(result.active()).isTrue();
        assertThat(result.createdBy()).isEqualTo(1L);

        verify(repository, times(1)).save(any(ProtectedArea.class));
    }

    @Test
    @DisplayName("Debería asignar color correcto para PARQUE_NACIONAL")
    void shouldAssignCorrectColorForParqueNacional() {
        // Given
        when(repository.save(any(ProtectedArea.class))).thenReturn(existingArea);

        // When
        protectedAreaService.createProtectedArea(createDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        ProtectedArea savedArea = areaCaptor.getValue();
        assertThat(savedArea.getColor()).isEqualTo("#FFFF00");
    }

    @Test
    @DisplayName("Debería asignar color correcto para BIC")
    void shouldAssignCorrectColorForBIC() {
        // Given
        CreateProtectedAreaDto bicDto = new CreateProtectedAreaDto(
                "Catedral",
                "Monumento BIC",
                ProtectedAreaType.MONUMENT,
                ProtectionType.BIC,
                "{\"type\":\"Point\",\"coordinates\":[0,0]}",
                "Galicia"
        );

        ProtectedArea bicArea = ProtectedArea.builder()
                .id(2L)
                .name("Catedral")
                .color("#FFD700")
                .build();

        when(repository.save(any(ProtectedArea.class))).thenReturn(bicArea);

        // When
        protectedAreaService.createProtectedArea(bicDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        ProtectedArea savedArea = areaCaptor.getValue();
        assertThat(savedArea.getColor()).isEqualTo("#FFD700");
    }

    @Test
    @DisplayName("Debería asignar color correcto para PATRIMONIO_ARQUEOLOGICO")
    void shouldAssignCorrectColorForPatrimonioArqueologico() {
        // Given
        CreateProtectedAreaDto patrimonioDto = new CreateProtectedAreaDto(
                "Castro",
                "Patrimonio arqueológico",
                ProtectedAreaType.MONUMENT,
                ProtectionType.PATRIMONIO_ARQUEOLOGICO,
                "{\"type\":\"Point\",\"coordinates\":[0,0]}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> {
            ProtectedArea area = inv.getArgument(0);
            area.setId(3L);
            return area;
        });

        // When
        protectedAreaService.createProtectedArea(patrimonioDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#D2B48C");
    }

    @Test
    @DisplayName("Debería asignar color correcto para LIC")
    void shouldAssignCorrectColorForLIC() {
        // Given
        CreateProtectedAreaDto licDto = new CreateProtectedAreaDto(
                "LIC Area",
                "Lugar de Importancia Comunitaria",
                ProtectedAreaType.AREA,
                ProtectionType.LIC,
                "{\"type\":\"Polygon\"}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(licDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#FF8C00");
    }

    @Test
    @DisplayName("Debería asignar color correcto para ZEPA")
    void shouldAssignCorrectColorForZEPA() {
        // Given
        CreateProtectedAreaDto zepaDto = new CreateProtectedAreaDto(
                "ZEPA Area",
                "Zona de Especial Protección para las Aves",
                ProtectedAreaType.AREA,
                ProtectionType.ZEPA,
                "{\"type\":\"Polygon\"}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(zepaDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#808080");
    }

    @Test
    @DisplayName("Debería asignar color correcto para RESERVA_BIOSFERA")
    void shouldAssignCorrectColorForReservaBiosfera() {
        // Given
        CreateProtectedAreaDto reservaDto = new CreateProtectedAreaDto(
                "Reserva",
                "Reserva de la Biosfera",
                ProtectedAreaType.AREA,
                ProtectionType.RESERVA_BIOSFERA,
                "{\"type\":\"Polygon\"}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(reservaDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#228B22");
    }

    @Test
    @DisplayName("Debería asignar color correcto para RED_NATURA_2000")
    void shouldAssignCorrectColorForRedNatura2000() {
        // Given
        CreateProtectedAreaDto redNaturaDto = new CreateProtectedAreaDto(
                "Red Natura",
                "Red Natura 2000",
                ProtectedAreaType.AREA,
                ProtectionType.RED_NATURA_2000,
                "{\"type\":\"Polygon\"}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(redNaturaDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#003399");
    }

    @Test
    @DisplayName("Debería asignar color correcto para PARQUE_NATURAL")
    void shouldAssignCorrectColorForParqueNatural() {
        // Given
        CreateProtectedAreaDto parqueDto = new CreateProtectedAreaDto(
                "Parque Natural",
                "Parque Natural",
                ProtectedAreaType.AREA,
                ProtectionType.PARQUE_NATURAL,
                "{\"type\":\"Polygon\"}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(parqueDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#90EE90");
    }

    @Test
    @DisplayName("Debería asignar color correcto para ESPACIO_NATURAL_PROTEGIDO")
    void shouldAssignCorrectColorForEspacioNaturalProtegido() {
        // Given
        CreateProtectedAreaDto espacioDto = new CreateProtectedAreaDto(
                "Espacio Natural",
                "Espacio Natural Protegido",
                ProtectedAreaType.AREA,
                ProtectionType.ESPACIO_NATURAL_PROTEGIDO,
                "{\"type\":\"Polygon\"}",
                "Galicia"
        );

        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(espacioDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#00CED1");
    }

    @Test
    @DisplayName("Debería marcar el área como activa al crear")
    void shouldMarkAreaAsActiveWhenCreating() {
        // Given
        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(createDto, 1L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().isActive()).isTrue();
    }

    @Test
    @DisplayName("Debería asignar adminId como createdBy")
    void shouldAssignAdminIdAsCreatedBy() {
        // Given
        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.createProtectedArea(createDto, 5L);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getCreatedBy()).isEqualTo(5L);
    }

    // ========== TESTS DE updateProtectedArea ==========

    @Test
    @DisplayName("Debería actualizar un área protegida correctamente")
    void shouldUpdateProtectedAreaSuccessfully() {
        // Given
        CreateProtectedAreaDto updateDto = new CreateProtectedAreaDto(
                "Nombre actualizado",
                "Descripción actualizada",
                ProtectedAreaType.AREA,
                ProtectionType.PARQUE_NATURAL,
                "{\"type\":\"Polygon\",\"coordinates\":[[[0,0]]]}",
                "Asturias"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existingArea));
        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ProtectedAreaDto result = protectedAreaService.updateProtectedArea(1L, updateDto);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        ProtectedArea updatedArea = areaCaptor.getValue();
        assertThat(updatedArea.getName()).isEqualTo("Nombre actualizado");
        assertThat(updatedArea.getDescription()).isEqualTo("Descripción actualizada");
        assertThat(updatedArea.getType()).isEqualTo(ProtectedAreaType.AREA);
        assertThat(updatedArea.getProtectionType()).isEqualTo(ProtectionType.PARQUE_NATURAL);
        assertThat(updatedArea.getCcaa()).isEqualTo("Asturias");
        assertThat(updatedArea.getColor()).isEqualTo("#90EE90");

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería actualizar el color si cambia el tipo de protección")
    void shouldUpdateColorWhenProtectionTypeChanges() {
        // Given
        CreateProtectedAreaDto updateDto = new CreateProtectedAreaDto(
                "Nombre",
                "Descripción",
                ProtectedAreaType.MONUMENT,
                ProtectionType.BIC,
                "{\"geometry\"}",
                "Galicia"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existingArea));
        when(repository.save(any(ProtectedArea.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        protectedAreaService.updateProtectedArea(1L, updateDto);

        // Then
        ArgumentCaptor<ProtectedArea> areaCaptor = ArgumentCaptor.forClass(ProtectedArea.class);
        verify(repository).save(areaCaptor.capture());

        assertThat(areaCaptor.getValue().getColor()).isEqualTo("#FFD700");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el área no existe al actualizar")
    void shouldThrowExceptionWhenAreaNotFoundOnUpdate() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> protectedAreaService.updateProtectedArea(999L, createDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Protected area not found");

        verify(repository, never()).save(any());
    }

    // ========== TESTS DE deleteProtectedArea ==========

    @Test
    @DisplayName("Debería eliminar un área protegida correctamente")
    void shouldDeleteProtectedAreaSuccessfully() {
        // Given
        doNothing().when(repository).deleteById(1L);

        // When
        protectedAreaService.deleteProtectedArea(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }

    // ========== TESTS DE getById ==========

    @Test
    @DisplayName("Debería obtener un área protegida por ID")
    void shouldGetProtectedAreaById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(existingArea));

        // When
        ProtectedAreaDto result = protectedAreaService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Parque Nacional de Galicia");
        assertThat(result.type()).isEqualTo(ProtectedAreaType.AREA);

        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el área no existe al buscar por ID")
    void shouldThrowExceptionWhenAreaNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> protectedAreaService.getById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Protected area not found");
    }

    // ========== TESTS DE getAllActive ==========

    @Test
    @DisplayName("Debería obtener todas las áreas protegidas activas")
    void shouldGetAllActiveProtectedAreas() {
        // Given
        ProtectedArea area2 = ProtectedArea.builder()
                .id(2L)
                .name("Área 2")
                .active(true)
                .type(ProtectedAreaType.AREA)
                .protectionType(ProtectionType.LIC)
                .color("#FF8C00")
                .build();

        when(repository.findAllActive()).thenReturn(Arrays.asList(existingArea, area2));

        // When
        List<ProtectedAreaDto> result = protectedAreaService.getAllActive();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Parque Nacional de Galicia");
        assertThat(result.get(1).name()).isEqualTo("Área 2");
        assertThat(result.get(0).active()).isTrue();
        assertThat(result.get(1).active()).isTrue();

        verify(repository, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay áreas activas")
    void shouldReturnEmptyListWhenNoActiveAreas() {
        // Given
        when(repository.findAllActive()).thenReturn(List.of());

        // When
        List<ProtectedAreaDto> result = protectedAreaService.getAllActive();

        // Then
        assertThat(result).isEmpty();
        verify(repository, times(1)).findAllActive();
    }

    // ========== TESTS DE getByCcaa ==========

    @Test
    @DisplayName("Debería obtener áreas protegidas por CCAA")
    void shouldGetProtectedAreasByCcaa() {
        // Given
        ProtectedArea area2 = ProtectedArea.builder()
                .id(2L)
                .name("Área Galicia 2")
                .ccaa("Galicia")
                .active(true)
                .type(ProtectedAreaType.AREA)
                .protectionType(ProtectionType.ZEPA)
                .color("#808080")
                .build();

        when(repository.findByCcaa("Galicia")).thenReturn(Arrays.asList(existingArea, area2));

        // When
        List<ProtectedAreaDto> result = protectedAreaService.getByCcaa("Galicia");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).ccaa()).isEqualTo("Galicia");
        assertThat(result.get(1).ccaa()).isEqualTo("Galicia");

        verify(repository, times(1)).findByCcaa("Galicia");
    }

    @Test
    @DisplayName("Debería retornar lista vacía si no hay áreas en la CCAA")
    void shouldReturnEmptyListWhenNoCcaaAreas() {
        // Given
        when(repository.findByCcaa("Madrid")).thenReturn(List.of());

        // When
        List<ProtectedAreaDto> result = protectedAreaService.getByCcaa("Madrid");

        // Then
        assertThat(result).isEmpty();
        verify(repository, times(1)).findByCcaa("Madrid");
    }

    // ========== TESTS DE isLocationProtected ==========

    @Test
    @DisplayName("Debería retornar true si la ubicación está protegida")
    void shouldReturnTrueWhenLocationIsProtected() {
        // Given
        when(repository.existsByCoordinates(43.5, -8.0)).thenReturn(true);

        // When
        boolean result = protectedAreaService.isLocationProtected(43.5, -8.0);

        // Then
        assertThat(result).isTrue();
        verify(repository, times(1)).existsByCoordinates(43.5, -8.0);
    }

    @Test
    @DisplayName("Debería retornar false si la ubicación no está protegida")
    void shouldReturnFalseWhenLocationIsNotProtected() {
        // Given
        when(repository.existsByCoordinates(40.0, -3.0)).thenReturn(false);

        // When
        boolean result = protectedAreaService.isLocationProtected(40.0, -3.0);

        // Then
        assertThat(result).isFalse();
        verify(repository, times(1)).existsByCoordinates(40.0, -3.0);
    }
}