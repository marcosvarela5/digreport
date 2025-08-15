package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.port.in.command.UserRegistrationCommand;
import es.marcos.digreport.domain.model.Member;

public interface MemberAuthService {

    public Member registerMember(UserRegistrationCommand userRegistrationCommand);
}
