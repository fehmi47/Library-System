package com.example.demo.service;

import com.example.demo.dto.GorevliKayitRequest;
import com.example.demo.entity.Gorevli;
import com.example.demo.repository.GorevliRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GorevliService {

    private final GorevliRepository gorevliRepository;
    private final PasswordEncoder passwordEncoder;

    public GorevliService(GorevliRepository gorevliRepository, PasswordEncoder passwordEncoder) {
        this.gorevliRepository = gorevliRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Gorevli kayitGorevli(GorevliKayitRequest request) {
        if (gorevliRepository.findByEposta(request.getEposta()).isPresent()) {
            throw new IllegalArgumentException("Bu e-posta zaten kullanımda.");
        }

        Gorevli gorevli = new Gorevli();
        gorevli.setAd(request.getAd());
        gorevli.setSoyad(request.getSoyad());
        gorevli.setEposta(request.getEposta());
        gorevli.setRol(request.getRol());
        gorevli.setTelefonNo(request.getTelefonNo());

        // ÖNEMLİ: Şifreyi kaydetmeden önce hash'liyoruz
        gorevli.setSifre(passwordEncoder.encode(request.getSifre()));

        return gorevliRepository.save(gorevli);
    }

    public List<Gorevli> tumGorevlileriGoster(){
        return gorevliRepository.findAll();
    }
}