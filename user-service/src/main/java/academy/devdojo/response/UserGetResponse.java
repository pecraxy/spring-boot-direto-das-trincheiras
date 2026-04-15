package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UserGetResponse {
    @Schema(description = "User's id", example = "1")
    private Long id;
    @Schema(example = "Jones", description = "User's first name.")
    private String firstName;
    @Schema(example = "Manoel", description = "User's last name.")
    private String lastName;
    @Schema(example = "jonesmanoel@pcbr.com.br", description = "User's e-mail. Must be unique.")
    private String email;
}
