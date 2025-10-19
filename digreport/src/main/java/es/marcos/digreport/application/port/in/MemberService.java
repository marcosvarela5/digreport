package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.member.MemberStatsDto;

import java.util.List;

public interface MemberService {

    List<MemberDto> getAllMembers();

    List<MemberStatsDto> getMembersWithStats();
}