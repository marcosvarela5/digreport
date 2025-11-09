package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.model.ProtectedArea;
import java.util.List;
import java.util.Optional;

public interface ProtectedAreaRepositoryPort {
    ProtectedArea save(ProtectedArea area);
    Optional<ProtectedArea> findById(Long id);
    List<ProtectedArea> findAll();
    List<ProtectedArea> findAllActive();
    List<ProtectedArea> findByCcaa(String ccaa);
    void deleteById(Long id);
    boolean existsByCoordinates(double latitude, double longitude);
}