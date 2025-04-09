package academy.devdojo.service;

import academy.devdojo.domain.Producer;
import academy.devdojo.repository.ProducerHardCodedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProducerServiceTest {
    @InjectMocks
    private ProducerService service;

    @Mock
    private ProducerHardCodedRepository repository;

    private List<Producer> producerList;

    @BeforeEach
    void init() {
        Producer producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(LocalDateTime.now()).build();
        Producer producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(LocalDateTime.now()).build();
        Producer producer3 = Producer.builder().id(3L).name("Studios Ghibli").createdAt(LocalDateTime.now()).build();
        producerList = new ArrayList<>(List.of(producer1, producer2, producer3));
    }

    @Test
    @DisplayName("findAll returns a list with all producers when argument is null")
    @Order(1)
    void findAll_ReturnAllProducers_WhenArgumentIsNull() {
        BDDMockito.when(repository.findAll()).thenReturn(producerList);
        var producers = service.findAll(null);
        Assertions.assertThat(producers)
                .hasSize(producerList.size())
                .hasSameElementsAs(producerList);
    }

    @Test
    @DisplayName("findAll returns a list with found object when name exists")
    @Order(2)
    void findAll_ReturnFoundProducer_WhenNameExists() {
        var producer = producerList.getFirst();

        List<Producer> expectedProducersFound = Collections.singletonList(producer);

        BDDMockito.when(repository.findByName(producer.getName()))
                .thenReturn(expectedProducersFound);

        List<Producer> producersFound = service.findAll(producer.getName());

        Assertions.assertThat(producersFound)
                .hasSize(expectedProducersFound.size())
                .hasSameElementsAs(expectedProducersFound)
                .containsAll(expectedProducersFound);
    }

    @Test
    @DisplayName("findAll returns empty list when name is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() {
        var name = "not-found";
        BDDMockito.when(repository.findByName(name)).thenReturn(Collections.emptyList());
        List<Producer> foundProducers = service.findAll(name);
        Assertions.assertThat(foundProducers)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("findById returns a producer with given id when succesful")
    @Order(4)
    void findById_ReturnsProducerById_WhenSucessful() {
        var expectedProducer = producerList.getFirst();
        BDDMockito.when(repository.findById(expectedProducer.getId())).thenReturn(Optional.of(expectedProducer));
        Producer foundProducer = service.findByIdOrThrowNotFound(expectedProducer.getId());
        Assertions.assertThat(foundProducer)
                .isEqualTo(expectedProducer)
                .isNotNull();
    }

    @Test
    @DisplayName("findById throws ResponseStatusException when producer is not found")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenProducerIsNotFound() {
        var expectedProducer = producerList.getFirst();
        BDDMockito.when(repository.findById(expectedProducer.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findByIdOrThrowNotFound(expectedProducer.getId()))
                .isInstanceOf(ResponseStatusException.class);

    }

    @Order(6)
    @Test
    @DisplayName("save creates a producer")
    void save_CreatesAProducer_WhenSuccesful() {
        var producerToSave = Producer.builder().id(4L).name("Mappa").createdAt(LocalDateTime.now()).build();

        BDDMockito.when(repository.save(producerToSave)).thenReturn(producerToSave);

        Producer savedProducer = service.save(producerToSave);

        Assertions.assertThat(savedProducer).isEqualTo(producerToSave).hasNoNullFieldsOrProperties();
    }

    @Order(7)
    @Test
    @DisplayName("delete deletes a producer")
    void delete_DeletesAProducer_WhenSuccesful() {
        var expectedProducerToDelete = producerList.getFirst();

        BDDMockito.when(repository.findById(expectedProducerToDelete.getId())).thenReturn(Optional.of(expectedProducerToDelete));

        BDDMockito.doNothing().when(repository).delete(expectedProducerToDelete);

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.delete(expectedProducerToDelete.getId()));
    }

    @Order(8)
    @Test
    @DisplayName("delete throws ResponseStatusException when producer is not found")
    void delete_ThrowsResponseStatusException_WhenProducerNotFound() {
        var expectedProducerToDelete = producerList.getFirst();

        BDDMockito.when(repository.findById(expectedProducerToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(expectedProducerToDelete.getId()))
                .isInstanceOf(ResponseStatusException.class);


    }

    @Order(9)
    @Test
    @DisplayName("update update a producer")
    void update_UpdateProducer_WhenSuccesfull() {
        var producerToUpdate = producerList.getFirst();

        BDDMockito.when(repository.findById(producerToUpdate.getId())).thenReturn(Optional.of(producerToUpdate));

        producerToUpdate.setName("Aniplex");

        BDDMockito.doNothing().when(repository).update(producerToUpdate);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(producerToUpdate));
    }

    @Order(10)
    @Test
    @DisplayName("update throws ResponseStatusException when producer is not found")
    void update_ThrowsResponseStatusException_WhenProducerNotFound() {
        var producerToUpdate = producerList.getFirst();

        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(producerToUpdate))
                .isInstanceOf(ResponseStatusException.class);
    }
}