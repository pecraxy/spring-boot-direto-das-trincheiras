package academy.devdojo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileGetResponse {
    public record User(Long id, String firstName) {}

    public record Profile(Long id, String name) {}

    private Long id;
    private User user;
    private Profile profile;
}
