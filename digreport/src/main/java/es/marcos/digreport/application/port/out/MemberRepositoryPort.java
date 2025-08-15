package es.marcos.digreport.application.port.out;

import es.marcos.digreport.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryPort {
    Member save(Member member);
    Optional<Member> findById(Long id);
    List<Member> findAll();
    void deleteById(Long id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByDni(String dni);
    Boolean existsByEmail(String email);
    Boolean existsByDni(String dni);
}
