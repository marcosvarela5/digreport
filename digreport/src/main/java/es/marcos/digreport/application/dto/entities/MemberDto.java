package es.marcos.digreport.application.dto.entities;

import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.model.Member;
import es.marcos.digreport.infrastructure.persistence.entities.MemberEntityJpa;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String name;
    private String surname1;
    private String surname2;
    private String email;
    private String dni;
    private String mobile;
    private UserRole role;
    private LocalDateTime registerDate;
    private String ccaa;

    public static MemberDto fromEntity(MemberEntityJpa entity) {
        return MemberDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname1(entity.getSurname1())
                .surname2(entity.getSurname2())
                .email(entity.getEmail())
                .dni(entity.getDni())
                .mobile(entity.getMobile())
                .role(entity.getRole())
                .registerDate(entity.getRegisterDate())
                .ccaa(entity.getCcaa())
                .build();
    }

    public static MemberDto fromDomain(Member domain) {
        return MemberDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .surname1(domain.getSurname1())
                .surname2(domain.getSurname2())
                .email(domain.getEmail())
                .dni(domain.getDni())
                .mobile(domain.getMobile())
                .role(domain.getRole())
                .registerDate(domain.getRegisterDate())
                .ccaa(domain.getCcaa())
                .build();
    }
}