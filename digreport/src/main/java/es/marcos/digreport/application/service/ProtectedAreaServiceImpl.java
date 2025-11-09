package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.protectedarea.CreateProtectedAreaDto;
import es.marcos.digreport.application.dto.protectedarea.ProtectedAreaDto;
import es.marcos.digreport.application.port.in.ProtectedAreaService;
import es.marcos.digreport.application.port.out.ProtectedAreaRepositoryPort;
import es.marcos.digreport.domain.enums.ProtectionType;
import es.marcos.digreport.domain.model.ProtectedArea;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProtectedAreaServiceImpl implements ProtectedAreaService {

    private final ProtectedAreaRepositoryPort repository;

    public ProtectedAreaServiceImpl(ProtectedAreaRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ProtectedAreaDto createProtectedArea(CreateProtectedAreaDto dto, Long adminId) {
        // 🎨 Asignar color automáticamente según tipo de protección
        String color = getColorByProtectionType(dto.protectionType());

        ProtectedArea area = ProtectedArea.builder()
                .name(dto.name())
                .description(dto.description())
                .type(dto.type())
                .protectionType(dto.protectionType())
                .geometry(dto.geometry())
                .ccaa(dto.ccaa())
                .color(color)  // 🆕 Color automático
                .createdBy(adminId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        ProtectedArea saved = repository.save(area);
        return toDto(saved);
    }

    @Override
    @Transactional
    public ProtectedAreaDto updateProtectedArea(Long id, CreateProtectedAreaDto dto) {
        ProtectedArea area = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protected area not found"));

        // 🎨 Actualizar color si cambia el tipo de protección
        String color = getColorByProtectionType(dto.protectionType());

        area.setName(dto.name());
        area.setDescription(dto.description());
        area.setType(dto.type());
        area.setProtectionType(dto.protectionType());
        area.setGeometry(dto.geometry());
        area.setCcaa(dto.ccaa());
        area.setColor(color);  // 🆕
        area.setUpdatedAt(LocalDateTime.now());

        ProtectedArea updated = repository.save(area);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteProtectedArea(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProtectedAreaDto getById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Protected area not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectedAreaDto> getAllActive() {
        return repository.findAllActive().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectedAreaDto> getByCcaa(String ccaa) {
        return repository.findByCcaa(ccaa).stream()
                .map(this::toDto)
                .toList();
    }

    private String getColorByProtectionType(ProtectionType protectionType) {
        return switch (protectionType) {

            // MONUMENTOS
            case BIC -> "#FFD700";
            case PATRIMONIO_ARQUEOLOGICO -> "#D2B48C";

            // ÁREAS
            case LIC -> "#FF8C00";
            case ZEPA -> "#808080";
            case RESERVA_BIOSFERA -> "#228B22";
            case RED_NATURA_2000 -> "#003399";
            case PARQUE_NACIONAL -> "#FFFF00";
            case PARQUE_NATURAL -> "#90EE90";
            case ESPACIO_NATURAL_PROTEGIDO -> "#00CED1";
        };
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLocationProtected(double latitude, double longitude) {
        return repository.existsByCoordinates(latitude, longitude);
    }

    private ProtectedAreaDto toDto(ProtectedArea area) {
        return new ProtectedAreaDto(
                area.getId(),
                area.getName(),
                area.getDescription(),
                area.getType(),
                area.getProtectionType(),
                area.getGeometry(),
                area.getCcaa(),
                area.getColor(),
                area.getCreatedBy(),
                area.getCreatedAt(),
                area.getUpdatedAt(),
                area.isActive()
        );
    }
}