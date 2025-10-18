package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.infrastructure.persistence.entities.FindEntityJpa;
import es.marcos.digreport.infrastructure.persistence.mapper.FindMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class FindRepositoryAdapter implements FindRepositoryPort {

    private final SpringDataFindRepository repository;

    public FindRepositoryAdapter(SpringDataFindRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Find save(Find find) {
        FindEntityJpa entity = FindMapper.toEntity(find);
        FindEntityJpa saved = repository.save(entity);
        return FindMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Find> findById(Long id) {
        return repository.findById(id).map(FindMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Find> findAll() {
        return repository.findAllByOrderByDiscoveredAtDesc()
                .stream()
                .map(FindMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Find> findByReporterId(Long reporterId) {
        return repository.findByReporterIdOrderByDiscoveredAtDesc(reporterId)
                .stream()
                .map(FindMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Find> findByStatus(FindValidationStatus status) {
        return repository.findByStatusOrderByDiscoveredAtDesc(status)
                .stream()
                .map(FindMapper::toDomain)
                .toList();
    }

    @Override
    public Boolean existsByReporterId(Long reporterId) {
        return repository.existsByReporterId(reporterId);
    }
}