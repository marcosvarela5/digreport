package es.marcos.digreport.infrastructure.persistence.mapper;

import es.marcos.digreport.domain.model.FindReviewNote;
import es.marcos.digreport.infrastructure.persistence.entities.FindReviewNoteEntityJpa;
import org.springframework.stereotype.Component;

@Component
public class FindReviewNoteMapper {

    public static FindReviewNote toDomain(FindReviewNoteEntityJpa entity) {
        if (entity == null) return null;

        FindReviewNote note = new FindReviewNote();
        note.setId(entity.getId());
        note.setFindId(entity.getFindId());
        note.setReviewerId(entity.getReviewerId());
        note.setComment(entity.getComment());
        note.setCreatedAt(entity.getCreatedAt());
        return note;
    }

    public static FindReviewNoteEntityJpa toEntity(FindReviewNote domain) {
        if (domain == null) return null;

        return FindReviewNoteEntityJpa.builder()
                .id(domain.getId())
                .findId(domain.getFindId())
                .reviewerId(domain.getReviewerId())
                .comment(domain.getComment())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}