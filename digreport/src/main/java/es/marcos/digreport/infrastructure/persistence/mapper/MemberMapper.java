package es.marcos.digreport.infrastructure.persistence.mapper;

import es.marcos.digreport.domain.model.Member;
import es.marcos.digreport.infrastructure.persistence.entities.MemberEntityJpa;
import org.springframework.stereotype.Component;


@Component
public class MemberMapper {

    public static MemberEntityJpa toEntity(Member member) {
        if (member == null) return null;
        return MemberEntityJpa.builder()
                .id(member.getId())
                .name(member.getName())
                .surname1(member.getSurname1())
                .surname2(member.getSurname2())
                .email(member.getEmail())
                .dni(member.getDni())
                .password(member.getPassword())
                .mobile(member.getMobile())
                .role(member.getRole())
                .registerDate(member.getRegisterDate())
                .ccaa(member.getCcaa())
                .reputation(member.getReputation())
                .build();
    }

    public static Member toDomain(MemberEntityJpa entity) {
        if (entity == null) return null;
        return new Member(
                entity.getId(),
                entity.getName(),
                entity.getSurname1(),
                entity.getSurname2(),
                entity.getEmail(),
                entity.getDni(),
                entity.getPassword(),
                entity.getMobile(),
                entity.getRole(),
                entity.getRegisterDate(),
                entity.getCcaa(),
                entity.getReputation()
        );
    }
}
