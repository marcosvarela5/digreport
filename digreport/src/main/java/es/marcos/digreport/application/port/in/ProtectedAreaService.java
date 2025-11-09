package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.protectedarea.CreateProtectedAreaDto;
import es.marcos.digreport.application.dto.protectedarea.ProtectedAreaDto;
import java.util.List;

public interface ProtectedAreaService {
    ProtectedAreaDto createProtectedArea(CreateProtectedAreaDto dto, Long adminId);
    ProtectedAreaDto updateProtectedArea(Long id, CreateProtectedAreaDto dto);
    void deleteProtectedArea(Long id);
    ProtectedAreaDto getById(Long id);
    List<ProtectedAreaDto> getAllActive();
    List<ProtectedAreaDto> getByCcaa(String ccaa);
    boolean isLocationProtected(double latitude, double longitude);
}