package com.example.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                // SecurityConfig.java içindeki authorizeHttpRequests kısmını şununla değiştir:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/app.js", "/style.css", "/admin.html", "/uye.html", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // Kitap işlemleri yetkileri
                        .requestMatchers("/api/kitap/liste").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/kitap/ekle/**", "/api/kitap/guncelle/**", "/api/kitap/sil/**").hasRole("LIBRARIAN")

                        // Kategori ve Yazar yetkileri
                        .requestMatchers("/api/kategori/**", "/api/yazar/**").hasRole("LIBRARIAN")

                        // Admin check noktası
                        .requestMatchers("/api/admin/check").hasRole("LIBRARIAN")

                        .anyRequest().authenticated()
                )
                // Kritik Nokta: Tarayıcı kutusunu (Popup) engelleyen kısım
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Yetkisiz Erişim: Lütfen giriş yapın.");
                }));

        return http.build();
    }
}


/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
    }
}
*/



