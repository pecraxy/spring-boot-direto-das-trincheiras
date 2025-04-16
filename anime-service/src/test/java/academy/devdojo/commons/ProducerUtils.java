package academy.devdojo.commons;

import academy.devdojo.domain.Producer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Component
public class ProducerUtils {
    public List<Producer> newProducerList(){
        String dateTime = "2025-04-10T16:38:32.2941297";
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Producer producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(localDateTime).build();
        Producer producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(localDateTime).build();
        Producer producer3 = Producer.builder().id(3L).name("Studios Ghibli").createdAt(localDateTime).build();
        return new ArrayList<>(List.of(producer1, producer2, producer3));
    }

    public Producer newProducerToSave(){
        return Producer.builder().id(99L).name("Mappa").createdAt(LocalDateTime.now()).build();
    }
}
