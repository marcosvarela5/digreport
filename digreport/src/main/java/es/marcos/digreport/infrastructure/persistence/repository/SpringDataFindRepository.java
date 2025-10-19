package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.infrastructure.persistence.entities.FindEntityJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataFindRepository extends JpaRepository<FindEntityJpa, Long> {

    List<FindEntityJpa> findByReporterIdOrderByDiscoveredAtDesc(Long reporterId);

    List<FindEntityJpa> findByStatusOrderByDiscoveredAtDesc(FindValidationStatus status);

    List<FindEntityJpa> findAllByOrderByDiscoveredAtDesc();

    Boolean existsByReporterId(Long reporterId);

    List<FindEntityJpa> findByValidatedByOrderByDiscoveredAtDesc(Long validatedBy);
}