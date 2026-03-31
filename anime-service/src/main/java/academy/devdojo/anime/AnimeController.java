package academy.devdojo.anime;

import academy.devdojo.domain.Anime;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/animes")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Anime API", description = "Anime related endpoints")
public class AnimeController {

    private final AnimeMapper mapper;
    private final AnimeService service;

    @GetMapping
    public ResponseEntity<List<AnimeGetResponse>> findAll(@RequestParam(required = false) String name) {

        List<Anime> animes = service.findAll(name);

        List<AnimeGetResponse> response = mapper.toAnimeGetResponseList(animes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<AnimeGetResponse>> findAllPaginated(Pageable pageable) {
        log.debug("Request received to list all animes paginated");
        Page<AnimeGetResponse> pageAnime = service.findAllPaginated(pageable).map(mapper::toAnimeGetResponse);
        return ResponseEntity.ok(pageAnime);
    }

    @GetMapping("{id}")
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        Anime foundAnime = service.findByIdOrThrowNotFound(id);

        AnimeGetResponse response = mapper.toAnimeGetResponse(foundAnime);

        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnimePostResponse> save(@RequestBody @Valid AnimePostRequest request) {
        Anime anime = mapper.toAnime(request);
        Anime savedAnime = service.save(anime);
        AnimePostResponse response = mapper.toAnimePostResponse(savedAnime);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid AnimePutRequest request) {
        Anime animeToUpdate = mapper.toAnime(request);
        service.update(animeToUpdate);
        return ResponseEntity.noContent().build();
    }


}
