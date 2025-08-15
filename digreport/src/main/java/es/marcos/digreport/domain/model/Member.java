package es.marcos.digreport.domain.model;

import es.marcos.digreport.domain.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Member {

    private Long id;
    private String name;
    private String surname1;
    private String surname2; // opcional
    private String email;
    private String dni;
    private String password;
    private String mobile;
    private UserRole role;
    private LocalDateTime registerDate;
    private String ccaa;

    public Member(Long id, String name, String surname1, String surname2,
                  String email, String dni, String password, String mobile, UserRole role,
                  LocalDateTime registerDate, String ccaa) {
        this.id = id;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.email = email;
        this.dni = dni;
        this.password = password;
        this.mobile = mobile;
        this.role = role;
        this.registerDate = registerDate;
        this.ccaa = ccaa;
    }
}

