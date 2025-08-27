package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.entities.MemberDto;
import es.marcos.digreport.application.dto.profile.UpdateProfileRequest;
import es.marcos.digreport.application.port.in.MemberSelfService;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.exception.DuplicatedDniException;
import es.marcos.digreport.domain.exception.DuplicatedEmailException;
import es.marcos.digreport.domain.exception.ValidationException;
import es.marcos.digreport.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static es.marcos.digreport.domain.util.MemberUtils.isValidDni;
import static es.marcos.digreport.domain.util.MemberUtils.isValidEmail;

@Service
public class MemberSelfServiceImpl implements MemberSelfService {


    private final MemberRepositoryPort memberRepositoryPort;

    public MemberSelfServiceImpl(MemberRepositoryPort memberRepositoryPort) {
        this.memberRepositoryPort = memberRepositoryPort;
    }

    @Transactional
    @Override
    public MemberDto updateProfile(String currentEmail, UpdateProfileRequest request) {

        String normalizedCurrentEmail = currentEmail.trim().toLowerCase();

        // Buscar usuario actual
        Member member = memberRepositoryPort.findByEmail(normalizedCurrentEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + normalizedCurrentEmail));

        // Actualizar solo los campos que no son null
        if (request.name() != null) {
            member.setName(validateAndTrim(request.name(), "name"));
        }

        if (request.surname1() != null) {
            member.setSurname1(validateAndTrim(request.surname1(), "surname1"));
        }

        if (request.surname2() != null) {
            member.setSurname2(request.surname2().trim());
        }

        if (request.email() != null) {
            String newEmail = request.email().trim().toLowerCase();
            if (!isValidEmail(newEmail)) {
                throw new ValidationException("Format not valid: Email");
            }
            if (!newEmail.equals(normalizedCurrentEmail) && memberRepositoryPort.existsByEmail(newEmail)) {
                throw new DuplicatedEmailException(newEmail);
            }
            member.setEmail(newEmail);
        }

        if (request.dni() != null) {
            String newDni = request.dni().trim().toUpperCase();
            if (!isValidDni(newDni)) {
                throw new ValidationException("Format not valid: DNI");
            }
            if (!newDni.equals(member.getDni()) && memberRepositoryPort.existsByDni(newDni)) {
                throw new DuplicatedDniException(newDni);
            }
            member.setDni(newDni);
        }

        if (request.mobile() != null) {
            member.setMobile(validateAndTrim(request.mobile(), "mobile"));
        }

        if (request.ccaa() != null) {
            member.setCcaa(validateAndTrim(request.ccaa(), "ccaa"));
        }


        Member savedMember = memberRepositoryPort.save(member);
        return MemberDto.fromDomain(savedMember);
    }

    private String validateAndTrim(String value, String fieldName) {
        if (value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " can't be empty");
        }
        return value.trim();
    }
}
