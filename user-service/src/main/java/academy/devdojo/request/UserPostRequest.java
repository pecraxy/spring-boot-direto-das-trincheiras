package academy.devdojo.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPostRequest {

    private String firstName;
    private String lastName;
    private String email;
}
