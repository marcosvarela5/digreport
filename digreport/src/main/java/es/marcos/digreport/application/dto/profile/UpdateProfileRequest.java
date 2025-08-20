package es.marcos.digreport.application.dto.profile;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL) // Solo campos no nulos
public record UpdateProfileRequest(
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String name,

        @Size(min = 2, max = 50, message = "El primer apellido debe tener entre 2 y 50 caracteres")
        String surname1,

        @Size(max = 50, message = "El segundo apellido no puede tener más de 50 caracteres")
        String surname2,

        @Email(message = "El formato del email no es válido")
        String email,

        @Size(min = 9, max = 9, message = "El DNI debe tener 9 caracteres")
        String dni,

        @Size(min = 9, max = 15, message = "El móvil debe tener entre 9 y 15 caracteres")
        String mobile,

        String ccaa
) {
}
