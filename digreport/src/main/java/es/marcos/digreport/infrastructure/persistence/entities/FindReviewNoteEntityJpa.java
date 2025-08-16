package es.marcos.digreport.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "find_review_note")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindReviewNoteEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "find_id", nullable = false)
    private FindEntityJpa find;
}
