package kr.co.yahopet.chatservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(request -> request.anyRequest().authenticated())
            .oauth2Login(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
//            .csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }
}
