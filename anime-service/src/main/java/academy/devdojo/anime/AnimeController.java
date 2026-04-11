package academy.devdojo.anime;

import academy.devdojo.domain.Anime;
import academy.devdojo.exception.DefaultErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
    @Operation(
            summary = "Find all Animes in System",
            responses = {
                    @ApiResponse(description = "Get Animes",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimeGetResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<List<AnimeGetResponse>> findAll(@RequestParam(required = false) String name) {

        List<Anime> animes = service.findAll(name);

        List<AnimeGetResponse> response = mapper.toAnimeGetResponseList(animes);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get Paginated Animes",
            responses = {
                @ApiResponse(description = "Paginated Animes", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Page.class)))
            }
    )
    public ResponseEntity<Page<AnimeGetResponse>> findAllPaginated(@ParameterObject Pageable pageable) {
        log.debug("Request received to list all animes paginated");
        Page<AnimeGetResponse> pageAnime = service.findAllPaginated(pageable).map(mapper::toAnimeGetResponse);
        return ResponseEntity.ok(pageAnime);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get Anime by Id",
            responses = {
                    @ApiResponse(description = "Get Anime by its id",
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AnimeGetResponse.class))
                    ),
                    @ApiResponse(description = "Anime Not Found",
                            responseCode = "404",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DefaultErrorMessage.class))
                    )
            })
    public ResponseEntity<AnimeGetResponse> findById(@PathVariable Long id) {
        Anime foundAnime = service.findByIdOrThrowNotFound(id);

        AnimeGetResponse response = mapper.toAnimeGetResponse(foundAnime);

        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates new Anime in the System",
            responses = {
                    @ApiResponse(description = "Creates new Anime",
                            responseCode = "201",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AnimePostResponse.class))
                    ),
                    @ApiResponse(description = "Validation Error",
                            responseCode = "400",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    ),
            }
    )
    public ResponseEntity<AnimePostResponse> save(@RequestBody @Valid AnimePostRequest request) {
        Anime anime = mapper.toAnime(request);
        Anime savedAnime = service.save(anime);
        AnimePostResponse response = mapper.toAnimePostResponse(savedAnime);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Deletes an Anime in the System",
            responses = {
                    @ApiResponse(description = "Deletes an Anime",
                            responseCode = "204",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(description = "Anime not found",
                            responseCode = "404",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    ),
            }
    )
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Updates an Anime in the System",
            responses = {
                    @ApiResponse(description = "Updates an Anime",
                            responseCode = "201",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AnimePostResponse.class))
                    ),
                    @ApiResponse(description = "Validation Error",
                            responseCode = "400",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    ),
                    @ApiResponse(description = "Anime not found",
                            responseCode = "404",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DefaultErrorMessage.class)
                            )
                    ),
            }
    )
    public ResponseEntity<Void> update(@RequestBody @Valid AnimePutRequest request) {
        Anime animeToUpdate = mapper.toAnime(request);
        service.update(animeToUpdate);
        return ResponseEntity.noContent().build();
    }
}
