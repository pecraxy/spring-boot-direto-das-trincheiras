package academy.devdojo.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Producer {
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String address;
    private LocalDateTime createdAt;
    @Getter
    private static List<Producer> producers = new ArrayList<>();

    static {
        Producer producer1 = Producer.builder().id(1L).name("Mappa").createdAt(LocalDateTime.now()).build();
        Producer producer2 = Producer.builder().id(2L).name("Madhouse").createdAt(LocalDateTime.now()).build();
        Producer producer3 = Producer.builder().id(3L).name("Kyoto Animation").createdAt(LocalDateTime.now()).build();
        producers.addAll(List.of(producer1, producer2, producer3));
    }

}
