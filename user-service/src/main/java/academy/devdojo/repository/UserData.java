package academy.devdojo.repository;

import academy.devdojo.domain.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserData {
    @Getter
    private final List<User> userList = new ArrayList<>(3);

    {
        User user1 = User.builder().id(1L).firstName("Marcelo").lastName("Sem dente").email("marcelosemdente@example.com").build();
        User user2 = User.builder().id(2L).firstName("William").lastName("Suane").email("williamsuane@example.com").build();
        User user3 = User.builder().id(3L).firstName("Rezende").lastName("Evil").email("rezendeevil@example.com").build();
        userList.addAll(List.of(user1, user2, user3));
    }


}
