package academy.devdojo.controller;

import academy.devdojo.domain.User;
import academy.devdojo.mapper.UserMapper;
import academy.devdojo.request.UserPostRequest;
import academy.devdojo.request.UserPutRequest;
import academy.devdojo.response.UserGetResponse;
import academy.devdojo.response.UserPostResponse;
import academy.devdojo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "User related endpoints")
public class UserController {
    private final UserMapper userMapper;
    private final UserService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserGetResponse>> findAll(@RequestParam(required = false) String firstName) {
        List<User> foundUsers = service.findAll(firstName);
        List<UserGetResponse> response = userMapper.toUserGetResponseList(foundUsers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserGetResponse> findByIdOrElseThrowResponseStatusException(@PathVariable Long id) {
        User foundUser = service.findByIdOrElseThrowNotFoundException(id);
        UserGetResponse response = userMapper.toUserGetResponse(foundUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserPostResponse> save(@RequestBody @Valid UserPostRequest request) {
        User userToSave = userMapper.toUser(request);
        User savedUser = service.save(userToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserPostResponse(savedUser));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UserPutRequest request) {
        User userToUpdate = userMapper.toUser(request);
        service.update(userToUpdate);
        return ResponseEntity.noContent().build();
    }
}
