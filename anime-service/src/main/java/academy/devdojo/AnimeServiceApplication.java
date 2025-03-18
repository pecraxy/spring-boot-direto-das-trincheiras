package academy.devdojo;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@Log4j2
public class AnimeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimeServiceApplication.class, args);
    }
}
