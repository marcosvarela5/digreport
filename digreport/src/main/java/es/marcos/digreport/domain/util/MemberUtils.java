package es.marcos.digreport.domain.util;

import es.marcos.digreport.domain.exception.ValidationException;

import java.util.regex.Pattern;

public final class MemberUtils {
    private MemberUtils() {

    }

    public static void requireNonBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Field '" + field + "' is required");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                .matcher(email)
                .matches();
    }

    public static boolean isValidDni(String dni) {
        return dni != null && Pattern.compile("^[0-9]{8}[A-Za-z]$")
                .matcher(dni)
                .matches();
    }
}
