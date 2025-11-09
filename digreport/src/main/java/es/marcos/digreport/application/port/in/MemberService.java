package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.member.MemberStatsDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {

    List<MemberDto> getAllMembers();

    List<MemberStatsDto> getMembersWithStats();

    Optional<MemberDto> findByEmail(String email);
}