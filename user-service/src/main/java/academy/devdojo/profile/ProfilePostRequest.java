package academy.devdojo.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfilePostRequest {
    @NotBlank(message = "The field 'name' is required")
    private String name;
    @NotBlank(message = "The field 'description' is required")
    private String description;
}
