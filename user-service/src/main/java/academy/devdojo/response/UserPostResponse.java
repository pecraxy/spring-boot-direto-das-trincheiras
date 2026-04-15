package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPostResponse {
    @Schema(description = "User's id", example = "99")
    private Long id;
}
