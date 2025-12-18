package com.example.demo.service;

import com.example.demo.entity.Gorevli;
import com.example.demo.entity.Uye;
import com.example.demo.repository.GorevliRepository;
import com.example.demo.repository.UyeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UyeRepository uyeRepository;
    private final GorevliRepository gorevliRepository;

    public CustomUserDetailsService(UyeRepository uyeRepository, GorevliRepository gorevliRepository) {
        this.uyeRepository = uyeRepository;
        this.gorevliRepository = gorevliRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String eposta) throws UsernameNotFoundException {

        // 1. Görevli tablosunda arama
        var gorevliOptional = gorevliRepository.findByEposta(eposta);
        if (gorevliOptional.isPresent()) {
            Gorevli gorevli = gorevliOptional.get();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(gorevli.getEposta())
                    .password(gorevli.getSifre())
                    // .roles() yerine .authorities() kullanarak tam kontrol sağlıyoruz
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_LIBRARIAN")))
                    .build();
        }

        // 2. Üye tablosunda arama
        var uyeOptional = uyeRepository.findByEposta(eposta);
        if (uyeOptional.isPresent()) {
            Uye uye = uyeOptional.get();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(uye.getEposta())
                    .password(uye.getSifre())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER")))
                    .build();
        }

        // 3. Kullanıcı hiçbir tabloda bulunamazsa
        throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + eposta);
    }
}