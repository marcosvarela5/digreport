package es.marcos.digreport.domain.model;

import es.marcos.digreport.domain.enums.FindPriority;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Find {

    private Long id;


    private LocalDateTime discoveredAt;
    private Double latitude;
    private Double longitude;
    private Long reporterId;


    private String description;

    private String ccaa;

    private Long validatedBy;

    private FindValidationStatus status = FindValidationStatus.PENDING;
    private FindPriority findPriority = FindPriority.MEDIUM;

    //private List<FindImage> images = new ArrayList<>();
    //private List<FindReviewNote> reviewNotes = new ArrayList<>();


    public Find() {
    }

    public Find(Long id,
                LocalDateTime discoveredAt,
                Double latitude,
                Double longitude,
                Long reporterId,
                String description,
                String ccaa,
                Long validatedBy
                /*List<FindImage> images*/) {
        this.id = id;
        this.discoveredAt = discoveredAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reporterId = reporterId;
        this.description = description;
        this.ccaa = ccaa;
        this.validatedBy = validatedBy;
        //this.images = images;
    }
}
