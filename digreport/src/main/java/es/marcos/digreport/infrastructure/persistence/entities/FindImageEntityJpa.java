package es.marcos.digreport.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "find_image")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindImageEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "find_id", nullable = false)
    private FindEntityJpa find;
}
