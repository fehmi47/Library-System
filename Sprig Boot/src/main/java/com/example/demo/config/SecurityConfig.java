package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Güvenli şifre saklama için BCrypt algoritmasını kullanıyoruz.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // POST istekleri için hayati önemde
                .cors(cors -> cors.disable()) // CORS kaynaklı bir engelleme ihtimaline karşı
                .authorizeHttpRequests(auth -> auth
                        // 1. İzin verilen yollar (En üstte olmalı)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()

                        // 2. Rol bazlı yollar
                        .requestMatchers("/api/admin/**", "/api/kategori/**", "/api/yazar/**", "/api/kitap/admin/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/uye/**", "/api/emanet/**", "/api/ceza/**").hasAnyRole("LIBRARIAN", "MEMBER")

                        // 3. Geri kalan her şey
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }}
