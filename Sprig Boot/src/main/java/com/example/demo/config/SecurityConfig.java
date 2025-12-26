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
                .csrf(csrf -> csrf.disable()) // CSRF korumasını kapatıyoruz (Test için)
                .cors(Customizer.withDefaults()) // CORS ayarları

                .authorizeHttpRequests(auth -> auth
                        // 1. HERKESE AÇIK ALANLAR (Statik dosyalar ve Login API'si)
                        // "uye.js" ve "app.js" burada olmazsa sayfa çalışmaz!
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/app.js",
                                "/uye.js",          // <--- Üye paneli JS dosyası
                                "/style.css",
                                "/admin.html",
                                "/uye.html",
                                "/favicon.ico"
                        ).permitAll()

                        // Login ve Register işlemleri herkese açık
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. KİTAP İŞLEMLERİ
                        // UserDetailsService "ROLE_" eklediği için artık hasAnyRole kullanıyoruz.
                        // hasAnyRole("MEMBER") arka planda "ROLE_MEMBER" arar ve senin kodunla EŞLEŞİR.
                        .requestMatchers("/api/kitap/liste").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/kitap/**").hasRole("LIBRARIAN")

                        // 3. EMANET VE CEZA İŞLEMLERİ (Üye Paneli İçin Kritik)
                        .requestMatchers("/api/emanet/**").hasAnyRole("LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/ceza/**").hasAnyRole("LIBRARIAN", "MEMBER")

                        // 4. ÜYE LİSTESİ (ID bulma işlemi için gerekli)
                        .requestMatchers("/api/uye/liste").hasAnyRole("LIBRARIAN", "MEMBER")

                        // 5. YÖNETİCİ ÖZEL ALANLARI
                        .requestMatchers("/api/uye/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/kategori/**", "/api/yazar/**").hasRole("LIBRARIAN")
                        .requestMatchers("/api/admin/**").hasRole("LIBRARIAN")

                        // Diğer tüm istekler için giriş şart
                        .anyRequest().authenticated()
                )
                // Tarayıcıda varsayılan giriş kutusunu (Popup) engellemek için:
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Giriş Başarısız: Lütfen e-posta ve şifrenizi kontrol edin.");
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



