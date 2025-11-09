package es.marcos.digreport.domain.model;

import es.marcos.digreport.domain.enums.ProtectedAreaType;
import es.marcos.digreport.domain.enums.ProtectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtectedArea {
    private Long id;
    private String name;
    private String description;
    private ProtectedAreaType type;
    private ProtectionType protectionType;
    private String geometry;
    private String ccaa;
    private String color;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
}