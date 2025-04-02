package academy.devdojo.config;

import external.dependency.Connection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionConfiguration {
    @Bean
    public Connection connectionMySql(){
        return new Connection("localhost", "devdojoMySql", "goku");
    }

    @Bean
    public Connection connectionMongoDB(){
        return new Connection("localhost", "devdojoMongoDB", "goku");
    }
}
