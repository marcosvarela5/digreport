package es.marcos.digreport.application.dto.find;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateFindRequest(
        @NotNull(message = "La fecha de descubrimiento es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDateTime discoveredAt,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", message = "Latitud inválida")
        @DecimalMax(value = "90.0", message = "Latitud inválida")
        Double latitude,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", message = "Longitud inválida")
        @DecimalMax(value = "180.0", message = "Longitud inválida")
        Double longitude,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String description,

        String ccaa
) {}