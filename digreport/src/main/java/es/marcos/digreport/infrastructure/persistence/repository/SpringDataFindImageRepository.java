package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.infrastructure.persistence.entities.FindImageEntityJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataFindImageRepository extends JpaRepository<FindImageEntityJpa, Long> {

    List<FindImageEntityJpa> findByFindIdOrderByDisplayOrderAsc(Long findId);

    void deleteByFindId(Long findId);

    Long countByFindId(Long findId);
}