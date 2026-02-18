package academy.devdojo.controller;

import academy.devdojo.domain.Profile;
import academy.devdojo.mapper.ProfileMapper;
import academy.devdojo.request.ProfilePostRequest;
import academy.devdojo.response.ProfileGetResponse;
import academy.devdojo.response.ProfilePostResponse;
import academy.devdojo.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<List<ProfileGetResponse>> listAll() {
        log.debug("Receiving request to list all profiles");

        List<Profile> profilesFound = service.findAll();

        List<ProfileGetResponse> response = mapper.toProfileGetResponseList(profilesFound);

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
