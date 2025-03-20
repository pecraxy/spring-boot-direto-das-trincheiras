package academy.devdojo.controller;

import academy.devdojo.domain.Producer;
import academy.devdojo.request.ProducerPostRequest;
import academy.devdojo.response.ProducerGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("v1/producers")
@Slf4j
public class ProducerController {


    @GetMapping
    public List<Producer> listAll(@RequestParam(required = false) String name) {
        if (name == null) return Producer.getProducers();
        return Producer.getProducers().stream()
                .filter(producer -> producer.getName().contains(name))
                .toList();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "x-api-key")
    public ResponseEntity<ProducerGetResponse> save(@RequestBody ProducerPostRequest producerPostRequest, @RequestHeader HttpHeaders headers) {
        log.info("{}", headers);
        Producer producer = Producer.builder()
                .id(ThreadLocalRandom.current().nextLong(1, 100))
                .name(producerPostRequest.getName())
                .createdAt(LocalDateTime.now())
                .build();
        Producer.getProducers().add(producer);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Authorization", "My Key");
        ProducerGetResponse response = ProducerGetResponse.builder()
                .id(producer.getId())
                .name(producer.getName())
                .createdAt(producer.getCreatedAt())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(response);
    }

    @GetMapping("{id}")
    public Producer findById(@PathVariable Long id) {
        return Producer.getProducers().stream()
                .filter(producer -> producer.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
