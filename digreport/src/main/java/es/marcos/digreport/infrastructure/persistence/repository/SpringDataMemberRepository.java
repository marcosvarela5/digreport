package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.infrastructure.persistence.entities.MemberEntityJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataMemberRepository extends JpaRepository<MemberEntityJpa, Long> {

    Optional<MemberEntityJpa> findByEmail(String email);
    Optional<MemberEntityJpa> findByDni(String dni);
    Long countByRole(UserRole role);
}
