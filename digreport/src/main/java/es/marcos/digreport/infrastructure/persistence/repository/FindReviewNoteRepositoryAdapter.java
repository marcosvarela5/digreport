package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.application.port.out.FindReviewNoteRepositoryPort;
import es.marcos.digreport.domain.model.FindReviewNote;
import es.marcos.digreport.infrastructure.persistence.mapper.FindReviewNoteMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FindReviewNoteRepositoryAdapter implements FindReviewNoteRepositoryPort {

    private final SpringDataFindReviewNoteRepository repository;

    public FindReviewNoteRepositoryAdapter(SpringDataFindReviewNoteRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public FindReviewNote save(FindReviewNote note) {
        return FindReviewNoteMapper.toDomain(
                repository.save(FindReviewNoteMapper.toEntity(note))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindReviewNote> findByFindId(Long findId) {
        return repository.findByFindIdOrderByCreatedAtDesc(findId)
                .stream()
                .map(FindReviewNoteMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindReviewNote> findByReviewerId(Long reviewerId) {
        return repository.findByReviewerIdOrderByCreatedAtDesc(reviewerId)
                .stream()
                .map(FindReviewNoteMapper::toDomain)
                .toList();
    }
}
