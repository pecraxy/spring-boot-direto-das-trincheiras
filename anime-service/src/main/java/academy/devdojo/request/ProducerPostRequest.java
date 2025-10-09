package academy.devdojo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ProducerPostRequest {
    @NotBlank(message = "The field 'name' is required")
    private String name;
}
