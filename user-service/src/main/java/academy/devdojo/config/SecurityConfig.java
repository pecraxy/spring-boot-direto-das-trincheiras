package academy.devdojo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    private static final String[] WHITELIST = {"/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/csrf"};

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
        var user = User.withUsername("zezin")
                .password(passwordEncoder.encode("labubu"))
                .roles("USER")
                .build();
        var admin = User.withUsername("admin")
                .password(passwordEncoder.encode("devdojo"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
//                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers(WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "v1/users").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "v1/users/*").hasRole("ADMIN")
                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
