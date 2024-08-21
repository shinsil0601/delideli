package kr.co.jhta.app.delideli.common.config;

import kr.co.jhta.app.delideli.common.security.JwtAuthenticationFilter;
import kr.co.jhta.app.delideli.common.security.JwtTokenProvider;
import kr.co.jhta.app.delideli.common.service.CombinedUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    @Lazy
    private CombinedUserDetailsService combinedUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/user/home/**", "/error",
                                "/user/login","/user/register", "/user/loginProc", "/user/findId", "/user/findPw", "/user/sendResetLink", "/user/userChangePw", "/user/changePassword", "/user/checkUserId", "/user/checkUserEmail", "/user/sendVerificationCode", "/user/verifyCode",
                                "/user/category/**", "/user/filterStoresByAddress", "/user/search/**", "/user/storeDetail/**", "/user/menuDetail/**",
                                "/user/notice", "/user/detail/**", "/user/event", "/user/eventDetail/**", "/user/getStoresByCategory",
                                "/client/login", "/client/register", "client/loginProc", "/client/findId", "/client/findPw", "/client/sendResetLink", "/client/clientChangePw", "/client/changePassword", "client/checkClientId", "client/checkClientEmail", "/client/sendVerificationCode", "/client/verifyCode",
                                "/admin/login", "admin/loginProc" ).permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/client/**").hasRole("CLIENT")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, combinedUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/favicon.ico",
                "/resources/**",
                "/static/**",
                "/public/**",
                "/webui/**",
                "/h2-console/**",
                "/configuration/**",
                "/swagger-resources/**",
                "/api-docs/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/common/css/**",
                "/common/js/**",
                "/common/images/**",
                "/common/vendor/**",
                "/user/css/**",
                "/user/js/**",
                "/user/images/**",
                "/client/css/**",
                "/client/js/**",
                "/client/images/**",
                "/admin/css/**",
                "/admin/js/**",
                "/admin/images/**"
        );
    }
}
