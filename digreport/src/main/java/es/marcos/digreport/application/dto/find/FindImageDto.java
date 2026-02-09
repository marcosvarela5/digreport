package es.marcos.digreport.application.dto.find;

public record FindImageDto(
        Long id,
        Long findId,
        String filename,
        String originalFilename,
        String filePath,
        Long fileSize,
        String mimeType,
        Integer displayOrder,
        Boolean isPrimary
) {}
