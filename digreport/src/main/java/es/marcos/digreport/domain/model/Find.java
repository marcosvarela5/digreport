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


    private ValidationStatus status = ValidationStatus.PENDING;
    private Priority priority = Priority.MEDIUM;

    //private List<FindImage> images = new ArrayList<>();
    //private List<FindReviewNote> reviewNotes = new ArrayList<>();


    public Find() {
    }

    public Find(Long id,
                LocalDateTime discoveredAt,
                Double latitude,
                Double longitude,
                Long reporterId,
                String description
                /*List<FindImage> images*/) {
        this.id = id;
        this.discoveredAt = discoveredAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reporterId = reporterId;
        this.description = description;
        //this.images = images;
    }
}
