package academy.devdojo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Producer {
    private Long id;
    private String name;
    @Getter
    private static List<Producer> producers = new ArrayList<>();
    static {
        Producer producer1 = Producer.builder().id(1L).name("Mappa").build();
        Producer producer2 = Producer.builder().id(2L).name("Madhouse").build();
        Producer producer3 = Producer.builder().id(3L).name("Kyoto Animation").build();
        producers.addAll(List.of(producer1, producer2, producer3));
    }

}
