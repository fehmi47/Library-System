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


    public CustomUserDetailsService(UyeRepository uyeRepository, GorevliRepository gorevliRepository) {
        this.uyeRepository = uyeRepository;
        this.gorevliRepository = gorevliRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String eposta) throws UsernameNotFoundException {

        //Görevli tablosunda bu e-posta arar
        var gorevliOptional = gorevliRepository.findByEposta(eposta);
        if (gorevliOptional.isPresent()) {
            Gorevli gorevli = gorevliOptional.get();

            String yetki = "ROLE_" + gorevli.getRol().toUpperCase();

            return User.builder()
                    .username(gorevli.getEposta())
                    .password(gorevli.getSifre())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(yetki)))
                    .build();
        }

        // Görevli değilse, Üye tablosunda bu e-posta arar
        var uyeOptional = uyeRepository.findByEposta(eposta);
        if (uyeOptional.isPresent()) {
            Uye uye = uyeOptional.get();

            String yetki = "ROLE_" + uye.getRol().toUpperCase();

            return User.builder()
                    .username(uye.getEposta())
                    .password(uye.getSifre())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority(yetki)))
                    .build();
        }

        //Hiçbir tabloda yoksa giriş reddedilir
        throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + eposta);
    }
}