package academy.devdojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UserPutRequest {
    @NotNull(message = "The user id cannot be null")
    @Positive(message = "The user id must be a positive number higher than one")
    @Schema(description = "User's id", example = "1")
    private Long id;
    @NotBlank(message = "The field 'firstName' is required")
    @Schema(example = "Jones", description = "User's first name.")
    private String firstName;
    @NotBlank(message = "The field 'lastName' is required")
    @Schema(example = "Manoel", description = "User's last name.")
    private String lastName;
    @NotBlank(message = "The field 'email' is required")
    @Schema(example = "jonesmanoel@pcbr.com.br", description = "User's e-mail. Must be unique.")
    @Email(message = "Email is not valid", regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
    private String email;
}
