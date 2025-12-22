package com.example.demo.service;

import com.example.demo.dto.EmanetDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
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

    @Transactional
    public Emanet oduncVer(EmanetDTO dto) {
        // 1. Giriş yapan kullanıcının e-postasını al
        String loginOlanEposta = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Kullanıcıyı ve Kitabı veritabanından bul
        Uye uye = uyeRepository.findByEposta(loginOlanEposta)
                .orElseThrow(() -> new RuntimeException("Giriş yapan üye bulunamadı!"));

        Kitap kitap = kitapRepository.findById(dto.getKitapId())
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));

        // 3. Stok Kontrolü
        if (kitap.getAdet() <= 0) {
            throw new RuntimeException("Bu kitap şu an stokta yok!");
        }

        // 4. Emanet Nesnesini Oluştur (DTO'dan Entity'ye dönüşüm)
        Emanet emanet = new Emanet();
        emanet.setUye(uye);
        emanet.setKitap(kitap);

        // Varsayılan görevliyi ata (Örn: ID 4)
        Gorevli sistemGorevlisi = gorevliRepository.findById(4)
                .orElseThrow(() -> new RuntimeException("Sistem görevlisi bulunamadı!"));
        emanet.setGorevli(sistemGorevlisi);

        // 5. Stok Güncelle ve Kaydet
        kitap.setAdet(kitap.getAdet() - 1);
        kitapRepository.save(kitap);

        return emanetRepository.save(emanet);
    }

    @Transactional
    public String iadeAl(Integer emanetId) {
        Emanet emanet = emanetRepository.findById(emanetId)
                .orElseThrow(() -> new RuntimeException("Emanet kaydı bulunamadı!"));

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
            ceza.setDurum("Ödenmedi");
            emanet.setCeza(ceza);
            mesaj = "İade edildi. Gecikme: " + gecikmeGunu + " gün. Ceza: " + cezaTutari + " TL.";
        }

        emanetRepository.save(emanet);
        return mesaj;
    }
}