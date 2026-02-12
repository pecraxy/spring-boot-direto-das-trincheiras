package academy.devdojo.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileGetResponse {
    private Long id;
    private String name;
    private String description;
}
