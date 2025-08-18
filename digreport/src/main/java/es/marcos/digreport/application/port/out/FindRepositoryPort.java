package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.model.Find;


import java.util.List;
import java.util.Optional;

public interface FindRepositoryPort {

    Find save(Find find);

    Optional<Find> findById(Long id);

    List<Find> findAll();

    void deleteById(Long id);

    Optional<Find> findByReporterId(Long id);

    Boolean existsByReporterId(Long id);
}

