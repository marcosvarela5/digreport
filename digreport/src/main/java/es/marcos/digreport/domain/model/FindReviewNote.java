package es.marcos.digreport.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FindReviewNote {
    private Long id;
    private Long findId;
    private Long reviewerId;
    private String comment;
    private LocalDateTime createdAt;

    public FindReviewNote() {}

    public FindReviewNote(Long id, Long findId, Long reviewerId, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.findId = findId;
        this.reviewerId = reviewerId;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}