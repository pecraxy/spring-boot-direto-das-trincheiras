package academy.devdojo.controller;

import academy.devdojo.domain.User;
import academy.devdojo.domain.UserProfile;
import academy.devdojo.mapper.UserProfileMapper;
import academy.devdojo.response.UserProfileGetResponse;
import academy.devdojo.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/user-profiles")
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService service;
    private final UserProfileMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserProfileGetResponse>> findAll() {
        log.debug("Request received to list all user profiles");
        List<UserProfile> userProfiles = service.findAll();
        List<UserProfileGetResponse> response = mapper.toUserProfileGetResponse(userProfiles);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profiles/{id}/users")
    public ResponseEntity<List<User>> findById(@PathVariable Long id) {
        List<User> response = service.findUsersByProfileId(id);
        return ResponseEntity.ok(response);
    }
}
