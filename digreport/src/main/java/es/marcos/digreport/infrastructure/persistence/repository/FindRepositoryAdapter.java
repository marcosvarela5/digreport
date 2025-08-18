package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.infrastructure.persistence.entities.FindEntityJpa;
import es.marcos.digreport.infrastructure.persistence.mapper.FindMapper;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


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
        return repository.findAll().stream().map(FindMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Find> findByReporterId(Long id) {
        return repository.findByReporterId(id).map(FindMapper::toDomain);
    }

    @Override
    public Boolean existsByReporterId(Long id) {
        return repository.findByReporterId(id).isPresent();
    }
}
