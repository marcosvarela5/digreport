package es.marcos.digreport.domain.entities;

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
    private String movil;
    private UserRole role;
    private LocalDateTime registerdate;
    private String ccaa;

    public Member(Long id, String name, String surname1, String surname2,
                  String email, String dni, String movil, UserRole role,
                  LocalDateTime registerdate, String ccaa) {
        this.id = id;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.email = email;
        this.dni = dni;
        this.movil = movil;
        this.role = role;
        this.registerdate = registerdate;
        this.ccaa = ccaa;
    }
}

