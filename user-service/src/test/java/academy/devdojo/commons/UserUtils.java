package academy.devdojo.commons;

import academy.devdojo.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtils {
    public List<User> newUserList() {
        User user1 = User.builder().id(1L).firstName("Sunless").lastName("Shadow").email("shadowslave@example.com").build();
        User user2 = User.builder().id(2L).firstName("Kai").lastName("Nightingale").email("kainightingale@example.com").build();
        User user3 = User.builder().id(3L).firstName("Nephis").lastName("Anvil").email("nephis@example.com").build();
        return new ArrayList<>(List.of(user1, user2, user3));
    }

    public User newUserToCreate(){
        return User.builder().id(4L).firstName("Asgore").lastName("Dreemur").email("asgore@example.com").build();
    }
}
