package academy.devdojo.repository;

import academy.devdojo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserHardCodedRepository {
    private final UserData userData;

    public List<User> findAll() {
        return userData.getUserList();
    }

    public List<User> findByName(String firstName, String lastName) {
        return userData.getUserList().stream()
                .filter(user -> user.getFirstName().equalsIgnoreCase(firstName) || user.getLastName().equalsIgnoreCase(lastName))
                .toList();
    }

    public List<User> findByEmail(String email) {
        return userData.getUserList().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .toList();
    }

    public Optional<User> findById(Long id) {
        return userData.getUserList().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public User save(User user) {
        userData.getUserList().add(user);
        return user;
    }

    public void delete(User userToDelete) {
        userData.getUserList().remove(userToDelete);
    }

    public void update(User userToUpdate) {
        delete(userToUpdate);
        userData.getUserList().add(userToUpdate);
    }
}
