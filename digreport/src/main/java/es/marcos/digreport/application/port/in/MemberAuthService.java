package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.auth.LoginRequest;
import es.marcos.digreport.application.dto.auth.LoginResponse;
import es.marcos.digreport.application.port.in.command.UserRegistrationCommand;
import es.marcos.digreport.domain.model.Member;

public interface MemberAuthService {

    Member registerMember(UserRegistrationCommand userRegistrationCommand);

    LoginResponse login(LoginRequest loginRequest);


}
