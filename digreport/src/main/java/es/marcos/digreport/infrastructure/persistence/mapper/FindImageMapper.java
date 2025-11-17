package es.marcos.digreport.infrastructure.persistence.mapper;

import es.marcos.digreport.domain.model.FindImage;
import es.marcos.digreport.infrastructure.persistence.entities.FindImageEntityJpa;
import org.springframework.stereotype.Component;

@Component
public class FindImageMapper {

    public static FindImageEntityJpa toEntity(FindImage domain) {
        if (domain == null) return null;

        return FindImageEntityJpa.builder()
                .id(domain.getId())
                .findId(domain.getFindId())
                .filename(domain.getFilename())
                .originalFilename(domain.getOriginalFilename())
                .filePath(domain.getFilePath())
                .fileSize(domain.getFileSize())
                .mimeType(domain.getMimeType())
                .uploadedAt(domain.getUploadedAt())
                .displayOrder(domain.getDisplayOrder())
                .isPrimary(domain.getIsPrimary())
                .build();
    }

    public static FindImage toDomain(FindImageEntityJpa entity) {
        if (entity == null) return null;

        FindImage domain = new FindImage();
        domain.setId(entity.getId());
        domain.setFindId(entity.getFindId());
        domain.setFilename(entity.getFilename());
        domain.setOriginalFilename(entity.getOriginalFilename());
        domain.setFilePath(entity.getFilePath());
        domain.setFileSize(entity.getFileSize());
        domain.setMimeType(entity.getMimeType());
        domain.setUploadedAt(entity.getUploadedAt());
        domain.setDisplayOrder(entity.getDisplayOrder());
        domain.setIsPrimary(entity.getIsPrimary());

        return domain;
    }
}