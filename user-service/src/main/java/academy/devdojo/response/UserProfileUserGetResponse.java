package academy.devdojo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileUserGetResponse {
    private Long id;
    private String firstName;
}
