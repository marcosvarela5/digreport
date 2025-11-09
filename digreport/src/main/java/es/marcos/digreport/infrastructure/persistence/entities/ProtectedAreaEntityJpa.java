package es.marcos.digreport.infrastructure.persistence.entities;

import es.marcos.digreport.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "protected_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtectedAreaEntityJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProtectedAreaType type;

    @Column(length = 7)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "protection_type", nullable = false, length = 50)
    private ProtectionType protectionType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String geometry;

    @Column(length = 100)
    private String ccaa;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active;
}