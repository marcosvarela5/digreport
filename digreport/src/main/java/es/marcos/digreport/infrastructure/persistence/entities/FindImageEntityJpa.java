package es.marcos.digreport.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "find_image")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindImageEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "find_id", nullable = false)
    private Long findId;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;
}