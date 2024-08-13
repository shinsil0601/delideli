/*
package kr.co.jhta.app.delideli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/user/notice", "/user/detail").permitAll()  //모두 접근 가능
                        .anyRequest().authenticated()  //그외 요청은 인증받은 사람만 접근 가능
                );
        return http.build();
    }
}
*/