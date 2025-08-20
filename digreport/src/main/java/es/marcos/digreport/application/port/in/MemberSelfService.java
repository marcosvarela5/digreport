package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.profile.UpdateProfileRequest;

public interface MemberSelfService {

    MemberDto updateProfile(String currentEmail, UpdateProfileRequest request);
}
