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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerHardCodedRepositoryTest {
    @InjectMocks
    private ProducerHardCodedRepository repository;
    @Mock
    private ProducerData producerData;

    private final List<Producer> producerList = new ArrayList<>();

    @BeforeEach
    void init() {
        Producer producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();
        Producer producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();
        Producer producer3 = Producer.builder().id(3L).name("Studios Ghibli").createdAt(LocalDateTime.now()).build();
        producerList.addAll(List.of(producer1, producer2, producer3));
    }

    @Test
    @DisplayName("findAll returns a list with all producers")
    @Order(1)
    void findAll_ReturnAllProducers_WhenSuccesful() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producers = repository.findAll();
        Assertions.assertThat(producers)
                .hasSize(producerList.size())
                .hasSameElementsAs(producerList);
    }

    @Order(2)
    @Test
    @DisplayName("findById returns a producer with given id")
    void findById_ReturnsProducer_WhenSuccesful_OrThrowsNotFoundException() {
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
    void findByName_ReturnsEmptyList_WhenNameIsNull() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producers = repository.findByName(null);
        Assertions.assertThat(producers)
                .isNotNull()
                .isEmpty();
    }

    @Order(4)
    @Test
    @DisplayName("findByName returns list with found object when name exists")
    void findByName_ReturnsFoundProducerInList_WhenNameIsFound() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var expectedProducer = producerList.getFirst();
        var producers = repository.findByName("Ufotable");
        Assertions.assertThat(producers)
                .isNotNull()
                .hasSize(1)
                .contains(expectedProducer);
    }

    @Order(5)
    @Test
    @DisplayName("save creates a producer")
    void save_CreatesAProducer_WhenSuccesful() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producerToSave = Producer.builder()
                .id(4L)
                .name("Mappa")
                .createdAt(LocalDateTime.now())
                .build();
        Producer savedProducer = repository.save(producerToSave);
        Assertions.assertThat(savedProducer)
                .isEqualTo(producerToSave)
                .hasNoNullFieldsOrProperties();

        Optional<Producer> producerSavedOptional = repository.findById(producerToSave.getId());
        Assertions.assertThat(producerSavedOptional)
                .isPresent()
                .contains(producerToSave);
    }

    @Order(6)
    @Test
    @DisplayName("delete removes a producer when succefull")
    void delete_RemovesAProducer_WhenSuccesful() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var producerToDelete = producerList.getFirst();
        repository.delete(producerToDelete);
        Assertions.assertThat(this.producerList)
                .doesNotContain(producerToDelete);

        List<Producer> producers = repository.findAll();

        Assertions.assertThat(producers)
                .isNotEmpty()
                .doesNotContain(producerToDelete);
    }

    @Order(7)
    @Test
    @DisplayName("update update a producer")
    void update_UpdateProducer_WhenSuccesfull() {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        Producer producerToUpdate = this.producerList.getFirst();
        producerToUpdate.setName("Aniplex");

        repository.update(producerToUpdate);

        Assertions.assertThat(this.producerList.contains(producerToUpdate));

        var producerUpdateOptional = repository.findById(producerToUpdate.getId());

        Assertions.assertThat(producerUpdateOptional).isPresent();
        Assertions.assertThat(producerUpdateOptional.get().getName().equals(producerToUpdate.getName()));
    }
}