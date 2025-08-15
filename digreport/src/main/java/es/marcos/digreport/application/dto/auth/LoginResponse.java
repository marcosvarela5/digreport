package es.marcos.digreport.application.dto.auth;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.domain.model.Member;
import lombok.Getter;

@Getter
public class LoginResponse {

    private String token;
    private MemberDto member;

    public LoginResponse(String token, MemberDto member) {
        this.token = token;
        this.member = member;
    }


}
