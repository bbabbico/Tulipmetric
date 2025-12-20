package project7.tulipmetric;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class testConfig {
    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(a -> a.anyRequest().permitAll()).build();
    }
}
