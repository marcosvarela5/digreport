package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.protectedarea.CreateProtectedAreaDto;
import es.marcos.digreport.application.dto.protectedarea.ProtectedAreaDto;
import es.marcos.digreport.application.port.in.ProtectedAreaService;
import es.marcos.digreport.application.port.out.ProtectedAreaRepositoryPort;
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
        ProtectedArea area = ProtectedArea.builder()
                .name(dto.name())
                .description(dto.description())
                .type(dto.type())
                .geometry(dto.geometry())
                .ccaa(dto.ccaa())
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

        area.setName(dto.name());
        area.setDescription(dto.description());
        area.setType(dto.type());
        area.setGeometry(dto.geometry());
        area.setCcaa(dto.ccaa());
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
                area.getCreatedBy(),
                area.getCreatedAt(),
                area.getUpdatedAt(),
                area.isActive()
        );
    }
}