package academy.devdojo.controller;

import academy.devdojo.domain.Producer;
import academy.devdojo.mapper.ProducerMapper;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.response.ProducerGetResponse;
import academy.devdojo.response.ProducerPostResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("v1/producers")
@Slf4j
public class ProducerController {
    private static final ProducerMapper MAPPER = ProducerMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<List<ProducerGetResponse>> listAll(@RequestParam(required = false) String name) {
        log.debug("Request received to list all producers, param name {}", name);
        var producers = Producer.getProducers();
        var producerGetResponseList = MAPPER.toProducerGetResponseList(producers);
        if (name == null) return ResponseEntity.ok(producerGetResponseList);
        var response = producerGetResponseList.stream()
                .filter(producer -> producer.getName().contains(name))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-key")
    public ResponseEntity<ProducerPostResponse> save(@RequestBody ProducerPostRequest request, @RequestHeader HttpHeaders headers) {
        log.debug("Request received to create a new producer, producer: {}", request);
        var producer = MAPPER.toProducer(request);
        Producer.getProducers().add(producer);
        var response = MAPPER.toProducerPostResponse(producer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProducerGetResponse> findById(@PathVariable Long id) {
        var response = Producer.getProducers().stream()
                .filter(producer -> producer.getId().equals(id))
                .findFirst()
                .map(MAPPER::toProducerGetResponse)
                .orElse(null);
        return ResponseEntity.ok(response);
    }
}
