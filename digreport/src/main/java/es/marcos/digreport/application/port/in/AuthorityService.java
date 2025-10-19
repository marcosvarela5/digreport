package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.authority.DashboardStatsDto;

public interface AuthorityService {
    DashboardStatsDto getDashboardStats();
}