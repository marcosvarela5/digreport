package es.marcos.digreport.application.dto.find;

import es.marcos.digreport.domain.enums.FindPriority;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import jakarta.validation.constraints.*;

public record ValidateFindRequest(
        @NotNull(message = "El estado es obligatorio")
        FindValidationStatus status,

        @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
        String reviewComment,

        FindPriority priority
) {
    public ValidateFindRequest {
        if (status != FindValidationStatus.VALIDATED && status != FindValidationStatus.REJECTED) {
            throw new IllegalArgumentException("Estado inválido para validación");
        }
    }
}