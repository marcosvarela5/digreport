package es.marcos.digreport.infrastructure.persistence.repository;

import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.model.Member;
import es.marcos.digreport.infrastructure.persistence.entities.MemberEntityJpa;
import es.marcos.digreport.infrastructure.persistence.mapper.MemberMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryAdapter implements MemberRepositoryPort {

    private final SpringDataMemberRepository repository;

    public MemberRepositoryAdapter(SpringDataMemberRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Member save(Member member) {
        MemberEntityJpa entity = MemberMapper.toEntity(member);
        MemberEntityJpa saved = repository.save(entity);
        return MemberMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return repository.findById(id).map(MemberMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return repository.findAll()
                .stream()
                .map(MemberMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email).map(MemberMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByDni(String dni) {
        return repository.findByDni(dni).map(MemberMapper::toDomain);
    }

    public Boolean existsByEmail(String email) {
        return repository.findByEmail(email).isPresent();
    }

    public Boolean existsByDni(String dni) {
        return repository.findByDni(dni).isPresent();
    }
}