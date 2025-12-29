package com.example.demo.service;

import com.example.demo.entity.Ceza;
import com.example.demo.repository.CezaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CezaService {
    private final CezaRepository cezaRepository;

    public CezaService(CezaRepository cezaRepository) {
        this.cezaRepository = cezaRepository;
    }

    public List<Ceza> tumCezalariGoster(){
        return cezaRepository.findAll();
    }

    public List<Ceza> benimCezalariGoster(){
        String girisYapanEposta = SecurityContextHolder.getContext().getAuthentication().getName();
        return cezaRepository.findByEmanet_Uye_Eposta(girisYapanEposta);
    }

    @Transactional
    public String cezaOde(Integer id) {
        Ceza ceza = cezaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Ceza kaydı bulunamadı!"));


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginOlanEposta = auth.getName();


        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_LIBRARIAN"));

        // Eğer admin değilse ve ceza kendine ait değilse hata fırlat
        if (!isAdmin && !ceza.getEmanet().getUye().getEposta().equals(loginOlanEposta)) {
            throw new RuntimeException("Bu ceza size ait değil, ödeyemezsiniz!");
        }


        if ("ÖDENDİ".equalsIgnoreCase(ceza.getDurum())) {
            return "Bu ceza zaten ödenmiş.";
        }


        ceza.setDurum("ÖDENDİ");
        ceza.setOdemeTarihi(LocalDate.now());

        cezaRepository.save(ceza);

        return "Ödeme işlemi başarıyla tamamlandı.";
    }
}
