package es.marcos.digreport.infrastructure.persistence.repository;


import es.marcos.digreport.infrastructure.persistence.entities.FindEntityJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataFindRepository extends JpaRepository<FindEntityJpa,Long> {

    Optional<FindEntityJpa> findByReporterId(Long id);
    Boolean existsByReporterId(Long id);
}
