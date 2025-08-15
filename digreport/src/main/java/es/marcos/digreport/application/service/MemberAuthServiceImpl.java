package es.marcos.digreport.application.service;

import es.marcos.digreport.application.port.in.MemberAuthService;
import es.marcos.digreport.application.port.in.command.UserRegistrationCommand;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.exception.DuplicatedDniException;
import es.marcos.digreport.domain.exception.DuplicatedEmailException;
import es.marcos.digreport.domain.exception.ValidationException;
import es.marcos.digreport.domain.model.Member;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static es.marcos.digreport.domain.util.MemberUtils.*;

@Transactional
@Service
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberRepositoryPort memberRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public MemberAuthServiceImpl(MemberRepositoryPort memberRepositoryPort) {
        this.memberRepositoryPort = memberRepositoryPort;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    @Override
    public Member registerMember(UserRegistrationCommand userRegistrationCommand) {

        requireNonBlank(userRegistrationCommand.getName(), "name");
        requireNonBlank(userRegistrationCommand.getSurname1(), "surname1");
        requireNonBlank(userRegistrationCommand.getEmail(), "email");
        requireNonBlank(userRegistrationCommand.getDni(), "dni");
        requireNonBlank(userRegistrationCommand.getMobile(), "mobile");
        requireNonBlank(userRegistrationCommand.getCcaa(), "ccaa");

        //normalize fields
        String email = userRegistrationCommand.getEmail().trim().toLowerCase();
        String dni = userRegistrationCommand.getDni().trim().toUpperCase();

        if (!isValidEmail(email)) {
            throw new ValidationException("Invalid email format");
        }
        if (!isValidDni(dni)) {
            throw new ValidationException("Invalid DNI format (expected 8 digits + letter)");
        }

        memberRepositoryPort.findByEmail(email).ifPresent(m -> { throw new DuplicatedEmailException(email); });
        memberRepositoryPort.findByDni(dni).ifPresent(m -> { throw new DuplicatedDniException(dni); });

        String hashedPassword = passwordEncoder.encode(userRegistrationCommand.getPassword());

        Member member = new Member(
                null,
                userRegistrationCommand.getName(),
                userRegistrationCommand.getSurname1(),
                userRegistrationCommand.getSurname2(),
                email,
                dni,
                hashedPassword,
                userRegistrationCommand.getMobile(),
                UserRole.USER,
                LocalDateTime.now(),
                userRegistrationCommand.getCcaa()
        );
        return memberRepositoryPort.save(member);
    }
}
