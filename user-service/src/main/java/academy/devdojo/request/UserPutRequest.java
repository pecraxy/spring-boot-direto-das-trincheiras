package academy.devdojo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UserPutRequest {
    @NotNull(message = "The user 'id' cannot be null")
    private Long id;
    @NotBlank(message = "The field 'firstName' is required")
    private String firstName; // não seja nulo, nem vazio, nem branco
    @NotBlank(message = "The field 'lastName' is required")
    private String lastName;
    @NotBlank(message = "The field 'email' is required")
    @Email(message = "Email is not valid", regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
    private String email;
}
