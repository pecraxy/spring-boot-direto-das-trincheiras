package academy.devdojo.controller;

import academy.devdojo.domain.Anime;
import academy.devdojo.domain.Anime;
import academy.devdojo.mapper.AnimeMapper;
import academy.devdojo.request.AnimePostRequest;
import academy.devdojo.request.AnimePutRequest;
import academy.devdojo.response.AnimeGetResponse;
import academy.devdojo.response.AnimePostResponse;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("v1/animes")
@Slf4j
public class AnimeController {

    private static final AnimeMapper MAPPER = Mappers.getMapper(AnimeMapper.class);

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> listAll(@RequestParam(required = false) String name) {
        log.debug("Request received to list all animes, param name {}", name);
        var animeList = Anime.getAnimes();
        var animeGetResponseList = MAPPER.toAnimeGetResponseList(animeList);
        if (name == null) return ResponseEntity.ok(animeGetResponseList);
        var response = animeGetResponseList.stream()
                .filter(anime -> anime.getName().contains(name))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnimePostResponse> save(@RequestBody AnimePostRequest request) {
        log.debug("Request received to create a new Anime, anime: {}", request);
        Anime anime = MAPPER.toAnime(request);
        Anime.getAnimes().add(anime);
        var response = MAPPER.toAnimePostResponse(anime);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        var response = Anime.getAnimes().stream()
                .filter(anime -> anime.getId().equals(id))
                .findFirst()
                .map(MAPPER::toAnimeGetResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.debug("Request to delete anime by id: {}", id);
        Anime animeToDelete = Anime.getAnimes().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
        Anime.getAnimes().remove(animeToDelete);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody AnimePutRequest request) {
        log.debug("Request to update anime: {}", request);
        Anime animeToRemove = Anime.getAnimes().stream()
                .filter(a -> a.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
        Anime.getAnimes().remove(animeToRemove);
        Anime animeUpdated = MAPPER.toAnime(request);
        Anime.getAnimes().add(animeUpdated);
        return ResponseEntity.noContent().build();
    }

}
