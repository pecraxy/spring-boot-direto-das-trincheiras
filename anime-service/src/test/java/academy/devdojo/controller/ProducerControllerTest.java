package academy.devdojo.controller;

import academy.devdojo.domain.Producer;
import academy.devdojo.repository.ProducerData;
import academy.devdojo.repository.ProducerHardCodedRepository;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = ProducerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ComponentScan(basePackages = "academy.devdojo")
class ProducerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProducerData producerData;

    @MockitoSpyBean
    private ProducerHardCodedRepository repository;

    @Autowired
    private ResourceLoader resourceLoader;

    private List<Producer> producerList;

    @BeforeEach
    void init() {
        String dateTime = "2025-04-10T16:38:32.2941297";
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Producer producer1 = Producer.builder().id(1L).name("Ufotable").createdAt(localDateTime).build();
        Producer producer2 = Producer.builder().id(2L).name("Wit Studio").createdAt(localDateTime).build();
        Producer producer3 = Producer.builder().id(3L).name("Studios Ghibli").createdAt(localDateTime).build();
        producerList = new ArrayList<>(List.of(producer1, producer2, producer3));
    }

    @Test
    @DisplayName("GET v1/producers returns a list with all producers when argument is null")
    @Order(1)
    void findAll_ReturnAllProducers_WhenArgumentIsNull() throws Exception {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var response = readResourceFile("producer/get-producer-null-name-200.json");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }
    @Test
    @DisplayName("GET v1/producers?name=Ufotable returns a list with found object when name exists")
    @Order(2)
    void findAll_ReturnFoundProducer_WhenNameExists() throws Exception {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var response = readResourceFile("producer/get-producer-ufotable-200.json");
        var name = "Ufotable";
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }
    @Test
    @DisplayName("GET v1/producers?name=x returns a empty list when name is not found")
    @Order(3)
    void findAll_ReturnsEmptyList_WhenNameIsNotFound() throws Exception {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var response = readResourceFile("producer/get-producer-x-200.json");
        var name = "x";
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers").param("name", name))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/producers/1 returns a producer with given id when succesful")
    @Order(4)
    void findById_ReturnsProducerById_WhenSucessful() throws Exception {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var response = readResourceFile("producer/get-producer-by-id-1-200.json");
        var id = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/producers/1 throws ResponseStatusException 404 when producer is not found")
    @Order(5)
    void findById_ThrowsResponseStatusException_WhenProducerIsNotFound() throws Exception {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var id = 99L;
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/producers/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found"));
    }

    @Order(6)
    @Test
    @DisplayName("POST v1/producers creates a producer")
    void save_CreatesAProducer_WhenSuccesful() throws Exception {
        var request = readResourceFile("producer/post-request-producer-200.json");
        var response = readResourceFile("producer/post-response-producer-201.json");

        var producerToSave = Producer.builder().id(99L).name("Mappa").build();
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(producerToSave);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/producers")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-api-key", "v1")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Order(7)
    @Test
    @DisplayName("PUT v1/producers update a producer")
    void update_UpdateProducer_WhenSuccessful() throws Exception{
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);
        var request = readResourceFile("producer/put-request-producer-200.json");
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/producers")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Order(8)
    @Test
    @DisplayName("PUT v1/producers throws ResponseStatusException 404 when producer is not found")
    void update_ThrowsResponseStatusException_WhenProducerIsNotFound() throws Exception{
        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        var request = readResourceFile("producer/put-request-producer-404.json");
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/v1/producers")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found"));
    }

    @Order(9)
    @Test
    @DisplayName("DELETE v1/producers/1 removes a producer")
    void delete_DeletesAProducer_WhenSuccessful() throws Exception {
        BDDMockito.when(producerData.getProducers()).thenReturn(producerList);

        var id = producerList.getFirst().getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/producers/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Order(10)
    @Test
    @DisplayName("DELETE v1/producers/99 throws ResponseStatusException 404 when producer is not found")
    void delete_ThrowsResponseStatusException_WhenProducerIsNotFound() throws Exception {
        BDDMockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());
        var id = 99L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/producers/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.status().reason("Producer not found"));
    }

    private String readResourceFile(String fileName) throws IOException {
        var file = resourceLoader.getResource("classpath:%s".formatted(fileName)).getFile();
        return new String(Files.readAllBytes(file.toPath()));
    }

}