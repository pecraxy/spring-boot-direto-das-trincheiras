package academy.devdojo;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@Log4j2
@ComponentScan(basePackages = {"outside.devdojo", "academy.devdojo"})
public class AnimeServiceApplication {
	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(AnimeServiceApplication.class, args);
		Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(log::info);
	}
}
