package es.marcos.digreport.application.dto.auth;

import es.marcos.digreport.domain.enums.UserRole;

import lombok.Getter;

@Getter
public class LoginResponse {


    private String token;
    private String username;
    private UserRole role;


    public LoginResponse(String token, String username, UserRole role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }


}
