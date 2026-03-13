package dev.oauth.resourceserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. NextAuth가 호출하는 /userinfo 경로를 허용 (인증 필요)
                        .requestMatchers("/userinfo").authenticated()
                        // 2. 기존 API 경로들 (필요에 따라 유지)
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers("/api/profile").hasAuthority("SCOPE_profile")
                        .anyRequest().authenticated()
                )
                // 3. JWT 기반 리소스 서버 설정 유지
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}