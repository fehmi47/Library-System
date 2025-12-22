package com.example.demo.service;

import com.example.demo.entity.Ceza;
import com.example.demo.repository.CezaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CezaService {
    private final CezaRepository cezaRepository;

    public CezaService(CezaRepository cezaRepository) {
        this.cezaRepository = cezaRepository;
    }

    public List<Ceza> tumCezalariGoster(){
        return cezaRepository.findAll();
    }

    public List<Ceza> benimCezalarıGoster(){
        String girişYapanEposta = SecurityContextHolder.getContext().getAuthentication().getName();
        return cezaRepository.findAll().stream()
                .filter(ceza -> ceza.getEmanet().getUye().getEposta().equals(girişYapanEposta))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public String cezaOde(Integer id) {
        // 1. Cezayı bul
        Ceza ceza = cezaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Ceza kaydı bulunamadı!"));

        // 2. Güvenlik Kontrolü: Giriş yapan kişi ile cezanın sahibi aynı mı?
        String loginOlanEposta = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!ceza.getEmanet().getUye().getEposta().equals(loginOlanEposta)) {
            throw new RuntimeException("Bu ceza size ait değil, ödeyemezsiniz!");
        }

        // 3. Zaten ödenmiş mi?
        if ("ÖDENDİ".equals(ceza.getDurum())) {
            return "Bu ceza zaten ödenmiş.";
        }

        // 4. Ödeme işlemini onayla
        ceza.setDurum("ÖDENDİ");
        ceza.setOdemeTarihi(LocalDate.now());

        cezaRepository.save(ceza);

        return "Ödeme işleminiz başarıyla tamamlandı. Borcunuz kapatıldı.";
    }
}
