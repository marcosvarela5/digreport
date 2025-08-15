package es.marcos.digreport.infrastructure.repository.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname1;
    private String surname2;
    private String email;
    private String dni;
    private String movil;
    private String role;
    private LocalDateTime registerdate;
    private String ccaa;
}