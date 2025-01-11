package kr.co.yahopet.chatservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Order(2)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(request -> request.anyRequest().authenticated())
            .oauth2Login(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
//            .csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

    @Order(1)
    @Bean
    public SecurityFilterChain consultantSecurityFilterChain(HttpSecurity httpSecurity)
        throws Exception {
        httpSecurity
            .securityMatcher("/consultants/**", "/login")
            .authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.POST, "/consultants").permitAll()
                .anyRequest().authenticated())
            .formLogin(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
