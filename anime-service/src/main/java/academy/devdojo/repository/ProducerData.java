package academy.devdojo.repository;

import academy.devdojo.domain.Producer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Component
public class ProducerData {
    @Getter
    private final List<Producer> producers = new ArrayList<>();

}
