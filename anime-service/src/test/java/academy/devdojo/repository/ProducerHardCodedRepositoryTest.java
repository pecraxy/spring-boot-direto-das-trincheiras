package academy.devdojo.repository;

import academy.devdojo.domain.Producer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    void findAll_ReturnAllProducers_WhenSuccefull(){
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producers = repository.findAll();
        Assertions.assertThat(producers)
                .hasSize(producerList.size())
                .hasSameElementsAs(producerList);
    }

    @Order(2)
    @Test
    @DisplayName("findById returns a producer with given id")
    void findById_ReturnsProducer_WhenSuccefull_OrThrowsNotFoundException(){
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var expectedProducer = producerList.getFirst();
        var producer = repository.findById(expectedProducer.getId());
        Assertions.assertThat(producer)
                .isPresent()
                .contains(expectedProducer);

    }

    @Order(3)
    @Test
    @DisplayName("findByName returns empty list when name is null")
    void findByName_ReturnsEmptyList_WhenNameIsNull(){
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producers = repository.findByName(null);
        Assertions.assertThat(producers)
                .isNotNull()
                .isEmpty();
    }

    @Order(4)
    @Test
    @DisplayName("findByName returns list with found object when name exists")
    void findByName_ReturnsFoundProducerInList_WhenNameIsFound(){
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var expectedProducer = producerList.getFirst();
        var producers = repository.findByName("Ufotable");
        Assertions.assertThat(producers)
                .isNotNull()
                .hasSize(1)
                .contains(expectedProducer);
    }
}