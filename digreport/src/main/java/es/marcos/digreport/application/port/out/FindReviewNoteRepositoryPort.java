package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.model.FindReviewNote;

import java.util.List;

public interface FindReviewNoteRepositoryPort {
    FindReviewNote save(FindReviewNote note);
    List<FindReviewNote> findByFindId(Long findId);
    List<FindReviewNote> findByReviewerId(Long reviewerId); // ← AÑADIR ESTA LÍNEA
}