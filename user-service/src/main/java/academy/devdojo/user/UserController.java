package academy.devdojo.user;

import academy.devdojo.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService service;

    @GetMapping
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
