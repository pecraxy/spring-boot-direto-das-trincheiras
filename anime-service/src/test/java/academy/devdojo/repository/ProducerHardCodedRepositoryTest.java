package academy.devdojo.repository;

import academy.devdojo.domain.Producer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProducerHardCodedRepositoryTest {
    @InjectMocks
    private ProducerHardCodedRepository repository;
    @Mock
    private ProducerData producerData;

    private final List<Producer> producerList = new ArrayList<>();

    @BeforeEach
    void init(){
        Producer producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();
        Producer producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();
        Producer producer3 = Producer.builder().id(3L).name("Studios Ghibli").createdAt(LocalDateTime.now()).build();
        producerList.addAll(List.of(producer1, producer2, producer3));
    }

    @Test
    @DisplayName("findAll returns a list with all producers")
    void findAll_ReturnAllProducers_WhenSuccefull(){
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producers = repository.findAll();
        Assertions.assertThat(producers).hasSize(producerList.size());
    }

}