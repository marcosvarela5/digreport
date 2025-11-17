package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.application.port.out.FindImageRepositoryPort;
import es.marcos.digreport.domain.model.FindImage;
import es.marcos.digreport.infrastructure.persistence.entities.FindImageEntityJpa;
import es.marcos.digreport.infrastructure.persistence.mapper.FindImageMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class FindImageRepositoryAdapter implements FindImageRepositoryPort {

    private final SpringDataFindImageRepository repository;

    public FindImageRepositoryAdapter(SpringDataFindImageRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public FindImage save(FindImage image) {
        FindImageEntityJpa entity = FindImageMapper.toEntity(image);
        FindImageEntityJpa saved = repository.save(entity);
        return FindImageMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public List<FindImage> saveAll(List<FindImage> images) {
        List<FindImageEntityJpa> entities = images.stream()
                .map(FindImageMapper::toEntity)
                .toList();

        List<FindImageEntityJpa> saved = repository.saveAll(entities);

        return saved.stream()
                .map(FindImageMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FindImage> findById(Long id) {
        return repository.findById(id)
                .map(FindImageMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindImage> findByFindId(Long findId) {
        return repository.findByFindIdOrderByDisplayOrderAsc(findId)
                .stream()
                .map(FindImageMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByFindId(Long findId) {
        repository.deleteByFindId(findId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByFindId(Long findId) {
        return repository.countByFindId(findId);
    }
}