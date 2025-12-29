package com.example.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                .csrf(csrf -> csrf.disable()) // Test aşamasında kapatıyoruz
                .cors(Customizer.withDefaults()) // CORS ayarlarını aktif et

                .authorizeHttpRequests(auth -> auth
                        // 1. STATİK DOSYALAR (HTML, JS, CSS) - Herkese Açık
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/app.js",
                                "/uye.js",
                                "/style.css",
                                "/admin.html",
                                "/uye.html",
                                "/favicon.ico"
                        ).permitAll()

                        // 2. KİMLİK DOĞRULAMA & ŞİFRE SIFIRLAMA - Herkese Açık (ÖNEMLİ KISIM)
                        .requestMatchers("/api/auth/login", "/api/auth/register,/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/forgot-password", "/api/auth/reset-password").permitAll()

                        // (Alternatif olarak /api/auth/** diyerek hepsini de açabilirsin ama üstteki daha güvenli)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 3. ROL BAZLI İZİNLER (Senin kodların)
                        .requestMatchers("/api/kitap/liste").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/kitap/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/emanet/**").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/ceza/**").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/uye/liste").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/uye/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/kategori/**", "/api/yazar/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/admin/**").hasRole("LIBRARIAN")

                        // 4. DİĞER HER ŞEY KİLİTLİ
                        .anyRequest().authenticated()
                )

                // Giriş başarısız olursa 401 dön
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Giriş Başarısız: Yetkiniz yok veya giriş yapmadınız.");
                }));

        return http.build();
    }

    // --- CORS AYARLARI (Frontend ve Backend rahat konuşsun diye) ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Tüm sitelerden gelen isteklere izin ver
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}