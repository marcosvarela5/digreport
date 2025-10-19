package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.FindReviewNote;

import java.util.List;
import java.util.Optional;

public interface FindRepositoryPort {

    Find save(Find find);

    Optional<Find> findById(Long id);

    List<Find> findAll();

    void deleteById(Long id);

    List<Find> findByReporterId(Long reporterId);

    List<Find> findByStatus(FindValidationStatus status);

    Boolean existsByReporterId(Long reporterId);
}