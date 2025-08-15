package es.marcos.digreport.domain.exception;

public class DuplicatedDniException extends RuntimeException {
    public DuplicatedDniException(String dni) {
        super("DNI already exists: " + dni);
    }
}
