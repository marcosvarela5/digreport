package es.marcos.digreport.domain.model;

import es.marcos.digreport.domain.enums.Priority;
import es.marcos.digreport.domain.enums.ValidationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Find {

    private Long id;


    private LocalDateTime discoveredAt;
    private Double latitude;
    private Double longitude;
    private Long reporterId;


    private String description;
    private List<String> images = new ArrayList<>();


    private ValidationStatus status = ValidationStatus.PENDING;
    private Priority priority = Priority.MEDIUM;
    private List<ReviewNote> reviewNotes = new ArrayList<>();

    public Find() {}

    public Find(Long id,
                LocalDateTime discoveredAt,
                Double latitude,
                Double longitude,
                Long reporterId,
                String description) {
        this.id = id;
        this.discoveredAt = discoveredAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reporterId = reporterId;
        this.description = description;
    }

    // utils
    public void addImage(String url) {
        if (url != null && !url.isBlank()) images.add(url);
    }

    public void addReviewNote(ReviewNote note) {
        if (note != null) reviewNotes.add(note);
    }


    @Setter
    @Getter
    public static class ReviewNote {
        private Long reviewerId;
        private String comment;
        private LocalDateTime createdAt;

        public ReviewNote() {}

        public ReviewNote(Long reviewerId, String comment, LocalDateTime createdAt) {
            this.reviewerId = reviewerId;
            this.comment = comment;
            this.createdAt = createdAt;
        }
    }
}
