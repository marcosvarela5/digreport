package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.infrastructure.persistence.entities.ProtectedAreaEntityJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProtectedAreaRepository extends JpaRepository<ProtectedAreaEntityJpa, Long> {

    List<ProtectedAreaEntityJpa> findByActiveTrue();

    List<ProtectedAreaEntityJpa> findByCcaa(String ccaa);
}