package es.marcos.digreport.infrastructure.repository.mapper;

import es.marcos.digreport.domain.entities.Member;
import es.marcos.digreport.infrastructure.repository.entities.MemberEntityJpa;

public class MemberMapper {

    public static MemberEntityJpa toEntity(Member member) {
        return MemberEntityJpa.builder()
                .id(member.getId())
                .name(member.getName())
                .surname1(member.getSurname1())
                .surname2(member.getSurname2())
                .email(member.getEmail())
                .dni(member.getDni())
                .movil(member.getMovil())
                .role(member.getRole())
                .registerdate(member.getRegisterdate())
                .ccaa(member.getCcaa())
                .build();
    }

    public static Member toDomain(MemberEntityJpa entity) {
        return new Member(
                entity.getId(),
                entity.getName(),
                entity.getSurname1(),
                entity.getSurname2(),
                entity.getEmail(),
                entity.getDni(),
                entity.getMovil(),
                entity.getRole(),
                entity.getRegisterdate(),
                entity.getCcaa()
        );
    }
}
