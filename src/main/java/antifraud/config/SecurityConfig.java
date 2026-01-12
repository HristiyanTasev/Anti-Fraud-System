package antifraud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)// For modifying requests via Postman
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                )
                .authorizeHttpRequests(requests -> requests                     // manage access
                        // POST for registering
                                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                                .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                                .requestMatchers("/error/**").permitAll()
                        // GET, DELETE, PUT for auth
                                .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyAuthority("ADMINISTRATOR", "SUPPORT")
                                .requestMatchers(HttpMethod.DELETE, "/api/auth/user/{username}").hasAuthority("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasAuthority("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasAuthority("ADMINISTRATOR")
                        // POST for transaction
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("MERCHANT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/history").hasAuthority("SUPPORT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/history/{number}").hasAuthority("SUPPORT")
                                .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAuthority("SUPPORT")
                        // POST, DELETE, GET for suspicious-ip
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAuthority("SUPPORT")
                                .requestMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/{ip}").hasAuthority("SUPPORT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAuthority("SUPPORT")
                        // POST, DELETE, GET for stolencard
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAuthority("SUPPORT")
                                .requestMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/{number}").hasAuthority("SUPPORT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAuthority("SUPPORT")
                                .anyRequest().authenticated()
                        // other matchers
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                // other configurations
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
