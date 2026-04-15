package academy.devdojo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPostResponse {
    @Schema(example = "99", description = "Unique Identifier created to new User")
    private Long id;
}
