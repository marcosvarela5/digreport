package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.infrastructure.persistence.entities.FindReviewNoteEntityJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataFindReviewNoteRepository extends JpaRepository<FindReviewNoteEntityJpa, Long> {
    List<FindReviewNoteEntityJpa> findByFindIdOrderByCreatedAtDesc(Long findId);
    List<FindReviewNoteEntityJpa> findByReviewerIdOrderByCreatedAtDesc(Long reviewerId);
}