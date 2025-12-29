package com.example.demo.service;

import com.example.demo.dto.EmanetDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EmanetService {

    private final EmanetRepository emanetRepository;
    private final KitapRepository kitapRepository;
    private final UyeRepository uyeRepository;
    private final GorevliRepository gorevliRepository;

    public EmanetService(EmanetRepository emanetRepository, KitapRepository kitapRepository,
                         UyeRepository uyeRepository, GorevliRepository gorevliRepository) {
        this.emanetRepository = emanetRepository;
        this.kitapRepository = kitapRepository;
        this.uyeRepository = uyeRepository;
        this.gorevliRepository = gorevliRepository;
    }

    public List<Emanet> tumEmanetler() {
        return emanetRepository.findAll();
    }

    public List<Emanet> getBenimEmanetlerim() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Uye uye = uyeRepository.findByEposta(email)
                .orElseThrow(() -> new RuntimeException("Giriş yapan üye bulunamadı!"));
        return emanetRepository.findAllByUye(uye);
    }

    @Transactional
    public Emanet oduncVer(EmanetDTO dto) {
        Uye hedefUye;

        //emaney verilecek üyeyi bulma aşamaları
        if (dto.getUyeId() != null) {
            hedefUye = uyeRepository.findById(dto.getUyeId())
                    .orElseThrow(() -> new RuntimeException("Üye bulunamadı!"));
        } else {
            String loginOlanEposta = SecurityContextHolder.getContext().getAuthentication().getName();
            hedefUye = uyeRepository.findByEposta(loginOlanEposta)
                    .orElseThrow(() -> new RuntimeException("Giriş yapan üye bulunamadı!"));
        }

        Kitap kitap = kitapRepository.findById(dto.getKitapId())
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));

        if (kitap.getAdet() <= 0) {
            throw new RuntimeException("Stok yetersiz! Kitap tükenmiş.");
        }

        //eğer kullanıcı kitabı ödünç almışsa aynı kitapı tekrar ödünç alamaz
        boolean zatenVar = emanetRepository.existsByUyeAndKitapAndGercekTeslimTarihiIsNull(hedefUye, kitap);
        if (zatenVar) {
            throw new RuntimeException("Bu kitabı zaten ödünç aldınız! Önce elinizdekini iade etmelisiniz.");
        }

        Emanet emanet = new Emanet();
        emanet.setUye(hedefUye);
        emanet.setKitap(kitap);

        emanet.setEmanetTarihi(LocalDate.now());
        emanet.setBeklenenTeslimTarihi(LocalDate.now().plusDays(15));

        //Sistemde emanet işlemini yapan kim olduğunu buluruz
        String aktifKullaniciEposta = SecurityContextHolder.getContext().getAuthentication().getName();
        var gorevliOptional = gorevliRepository.findByEposta(aktifKullaniciEposta);

        if (gorevliOptional.isPresent()) {
            emanet.setGorevli(gorevliOptional.get());
        } else {
            Gorevli varsayilanGorevli = gorevliRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Sistem hatası: Varsayılan görevli (ID:1) bulunamadı."));
            emanet.setGorevli(varsayilanGorevli);
        }
        kitap.setAdet(kitap.getAdet() - 1);
        kitapRepository.save(kitap);

        return emanetRepository.save(emanet);
    }


    @Transactional
    public String iadeAl(Integer emanetId) {
        Emanet emanet = emanetRepository.findById(emanetId)
                .orElseThrow(() -> new RuntimeException("Emanet kaydı bulunamadı!"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String girisYapanEmail = auth.getName();

        // iade alanın admin olup olmadığına bakılır
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN") || a.getAuthority().equals("LIBRARIAN"));

        //eğer giriş yapan admin değilse ve emanet ona ait değilse hata fırlatır
        if (!isAdmin && !emanet.getUye().getEposta().equals(girisYapanEmail)) {
            throw new RuntimeException("HATA: Yetkisiz işlem! Sadece kendi emanetlerinizi iade edebilirsiniz.");
        }

        //iade edilen bir emaneti tekrar iade etmemek için kontrol ederiz
        if (emanet.getGercekTeslimTarihi() != null) {
            return "Bu kitap zaten iade edilmiş.";
        }

        emanet.setGercekTeslimTarihi(LocalDate.now());

        Kitap kitap = emanet.getKitap();
        kitap.setAdet(kitap.getAdet() + 1);
        kitapRepository.save(kitap);

        long gecikmeGunu = ChronoUnit.DAYS.between(emanet.getBeklenenTeslimTarihi(), emanet.getGercekTeslimTarihi());
        String mesaj = "Kitap başarıyla iade edildi.";

        if (gecikmeGunu > 0) {
            BigDecimal cezaTutari = BigDecimal.valueOf(gecikmeGunu * 10.0);

            Ceza ceza = new Ceza();
            ceza.setEmanet(emanet);
            ceza.setCezaMiktari(cezaTutari);
            ceza.setCezaTarihi(LocalDate.now());
            ceza.setDurum("ÖDENMEDİ");


            emanet.setCeza(ceza);


            mesaj = "İade edildi. Gecikme: " + gecikmeGunu + " gün. Ceza: " + cezaTutari + " TL.";
        }

        emanetRepository.save(emanet);
        return mesaj;
    }


}