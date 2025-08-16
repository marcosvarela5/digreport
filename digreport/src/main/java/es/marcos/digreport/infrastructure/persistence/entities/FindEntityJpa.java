package es.marcos.digreport.infrastructure.persistence.entities;

import es.marcos.digreport.domain.enums.Priority;
import es.marcos.digreport.domain.enums.ValidationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "find")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "discovered_at", nullable = false)
    private LocalDateTime discoveredAt;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId; // FK a member.id


    @Column(name = "description")
    private String description;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ValidationStatus status = ValidationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;


    @OneToMany(mappedBy = "find", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FindImageEntityJpa> images = new ArrayList<>();

    @OneToMany(mappedBy = "find", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FindReviewNoteEntityJpa> reviewNotes = new ArrayList<>();

    /* ====================  ==================== */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FindEntityJpa that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
}
