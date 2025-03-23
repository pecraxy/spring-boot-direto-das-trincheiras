package academy.devdojo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder

public class ProducerPostResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
