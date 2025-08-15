package es.marcos.digreport.application.port.in.command;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationCommand{

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String surname1;

    @Size(max = 100)
    private String surname2;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String dni;

    @NotBlank
    @Size(max = 20)
    private String mobile;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
    @NotBlank
    private String ccaa;
}