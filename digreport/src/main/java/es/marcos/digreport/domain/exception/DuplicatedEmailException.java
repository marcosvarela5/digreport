package es.marcos.digreport.domain.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String email) {
        super("Email already exists: " + email);
    }
}