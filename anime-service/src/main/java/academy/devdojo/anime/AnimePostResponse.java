package academy.devdojo.anime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnimePostResponse {
    @Schema(example = "99", description = "Auto-generated ID")
    private Long id;
    @Schema(example = "Saiki Kusuo no Psi Nan")
    private String name;
}
