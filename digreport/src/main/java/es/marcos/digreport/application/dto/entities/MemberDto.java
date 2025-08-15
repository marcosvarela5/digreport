package es.marcos.digreport.application.dto.entities;

import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.model.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MemberDto {
    private String name;
    private String surname1;
    private String surname2;
    private String email;
    private String dni;
    private String mobile;
    private UserRole role;
    private LocalDateTime registerDate;
    private String ccaa;

    public static MemberDto from(Member m) {
        MemberDto dto = new MemberDto();
        dto.name = m.getName();
        dto.surname1 = m.getSurname1();
        dto.surname2 = m.getSurname2();
        dto.email = m.getEmail();
        dto.dni = m.getDni();
        dto.mobile = m.getMobile();
        dto.role = m.getRole();
        dto.registerDate = m.getRegisterDate();
        dto.ccaa = m.getCcaa();
        return dto;
    }
}