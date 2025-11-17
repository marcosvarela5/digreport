package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.model.FindImage;

import java.util.List;
import java.util.Optional;

/**
 * Port de salida para persistencia de imágenes de hallazgos
 */
public interface FindImageRepositoryPort {

    FindImage save(FindImage image);

    List<FindImage> saveAll(List<FindImage> images);

    Optional<FindImage> findById(Long id);

    List<FindImage> findByFindId(Long findId);

    void deleteById(Long id);

    void deleteByFindId(Long findId);

    Long countByFindId(Long findId);
}