package es.marcos.digreport.infrastructure.persistence.entities;

import es.marcos.digreport.domain.enums.FindPriority;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private FindValidationStatus status = FindValidationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private FindPriority findPriority = FindPriority.MEDIUM;


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
