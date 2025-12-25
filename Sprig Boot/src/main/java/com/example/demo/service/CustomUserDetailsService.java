package com.example.demo.service;

import com.example.demo.entity.Gorevli;
import com.example.demo.entity.Uye;
import com.example.demo.repository.GorevliRepository;
import com.example.demo.repository.UyeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UyeRepository uyeRepository;
    private final GorevliRepository gorevliRepository;

    // Constructor Injection (Bağımlılıkların enjekte edilmesi)
    public CustomUserDetailsService(UyeRepository uyeRepository, GorevliRepository gorevliRepository) {
        this.uyeRepository = uyeRepository;
        this.gorevliRepository = gorevliRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String eposta) throws UsernameNotFoundException {

        // 1. ADIM: Görevli tablosunda bu e-posta var mı?
        var gorevliOptional = gorevliRepository.findByEposta(eposta);
        if (gorevliOptional.isPresent()) {
            Gorevli gorevli = gorevliOptional.get();

            // Veritabanında "LIBRARIAN" tuttuğun için Spring Security standartlarına (ROLE_) çeviriyoruz
            String yetki = "ROLE_" + gorevli.getRol().toUpperCase();

            return User.builder()
                    .username(gorevli.getEposta())
                    .password(gorevli.getSifre())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(yetki)))
                    .build();
        }

        // 2. ADIM: Görevli değilse, Üye tablosunda bu e-posta var mı?
        var uyeOptional = uyeRepository.findByEposta(eposta);
        if (uyeOptional.isPresent()) {
            Uye uye = uyeOptional.get();

            // Veritabanında "MEMBER" tuttuğun için "ROLE_MEMBER" yapıyoruz
            String yetki = "ROLE_" + uye.getRol().toUpperCase();

            return User.builder()
                    .username(uye.getEposta())
                    .password(uye.getSifre())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(yetki)))
                    .build();
        }

        // 3. ADIM: Hiçbir tabloda yoksa giriş reddedilir
        throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + eposta);
    }
}