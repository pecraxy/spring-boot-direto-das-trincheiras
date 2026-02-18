package academy.devdojo.profile;

import academy.devdojo.domain.Profile;
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
@RequestMapping("v1/profiles")
@Slf4j
@RequiredArgsConstructor

public class ProfileController {
    private final ProfileService service;
    private final ProfileMapper mapper;

    @GetMapping
    public ResponseEntity<List<ProfileGetResponse>> listAll(@RequestParam(required = false) String name) {
        log.debug("Receiving request to list all profiles with parameter 'name' {}", name);

        List<Profile> profilesFound = service.findAll(name);

        List<ProfileGetResponse> response = mapper.toProfileGetResponseList(profilesFound);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ProfileGetResponse>> listAllPaginated(Pageable pageable) {
        log.debug("Receiving request to list paginated profiles");

        Page<Profile> profilesFound = service.findAllPaginated(pageable);

        Page<ProfileGetResponse> response = profilesFound.map(mapper::toProfileGetResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProfileGetResponse> findById(@PathVariable Long id) {
        log.debug("Receiving request to find profile by id with id '{}'", id);

        Profile foundProfile = service.findByIdOrThrowResponseStatusException(id);

        ProfileGetResponse response = mapper.toProfileGetResponse(foundProfile);

        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfilePostResponse> save(@Valid @RequestBody ProfilePostRequest profilePostRequest) {
        log.debug("Receiving request to save profile with payload {}", profilePostRequest);

        Profile profileToSave = mapper.toProfile(profilePostRequest);

        Profile savedProfile = service.save(profileToSave);

        ProfilePostResponse response = mapper.toProfilePostResponse(savedProfile);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
