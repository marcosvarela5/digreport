package es.marcos.digreport.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una imagen asociada a un hallazgo
 */
@Getter
@Setter
public class FindImage {

    private Long id;
    private Long findId;
    private String filename;
    private String originalFilename;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime uploadedAt;
    private Integer displayOrder;
    private Boolean isPrimary;

    public FindImage() {
        this.uploadedAt = LocalDateTime.now();
        this.displayOrder = 0;
        this.isPrimary = false;
    }

    public FindImage(Long findId, String filename, String originalFilename,
                     String filePath, Long fileSize, String mimeType) {
        this();
        this.findId = findId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }
}