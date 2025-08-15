package es.marcos.digreport.infrastructure.security;

import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.model.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepositoryPort repository;

    public CustomUserDetailsService(MemberRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email.toLowerCase();
        System.out.println("Searching for user with email: " + email);
        Member member = repository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario con email " + normalizedEmail));

        return new CustomUserDetails(member);
    }
}
