package es.marcos.digreport.domain.entities;

import java.time.LocalDateTime;

public class Member {

    private Long id;
    private String name;
    private String surname1;
    private String surname2; // opcional
    private String email;
    private String dni;
    private String movil;
    private String role;
    private LocalDateTime registerdate;
    private String ccaa;

    public Member(Long id, String name, String surname1, String surname2,
                  String email, String dni, String movil, String role,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getRegisterdate() {
        return registerdate;
    }

    public void setRegisterdate(LocalDateTime registerdate) {
        this.registerdate = registerdate;
    }

    public String getCcaa() {
        return ccaa;
    }

    public void setCcaa(String ccaa) {
        this.ccaa = ccaa;
    }
}

